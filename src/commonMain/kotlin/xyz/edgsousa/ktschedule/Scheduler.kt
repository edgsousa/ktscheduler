package xyz.edgsousa.ktschedule

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

typealias Task = Pair<PointInTime, () -> Unit>

/**
 * Usage:
 *  - launch { Scheduler.runScheduler() }
 *  -- all tasks will be run as individual co-routines on the scope used
 */
object Scheduler {
    private val LOG = LoggerFactory.getLogger(Scheduler::class)

    private val mut = Mutex(false)
    private val priorityQueue = mutableListOf<Pair<Instant?, Task>>()

    private val notifyChannel = Channel<Unit> { Channel.CONFLATED }

    suspend fun addToSchedule(task: Task) {
        mut.withLock {
            val taskNext = task.first.next()
            val idx = priorityQueue.binarySearchBy(taskNext) { it.first }.let { if (it < 0) -(it + 1) else it + 1 }
            priorityQueue.add(idx, taskNext to task)
        }
        LOG.trace("Notify")
        notifyChannel.offer(Unit)
    }

    private fun Instant.timeToTaskMillis(taskNext: Instant?): Long? {
        return taskNext?.let { ChronoUnit.MILLIS.between(this, it) }
    }

    private fun <T> MutableList<T>.removeWhile(select: (T) -> Boolean): Iterable<T> {
        val destination = mutableListOf<T>()
        this.iterator().run {
            while (hasNext()) {
                val n = next()
                if (select(n)) {
                    remove()
                    destination.add(n)
                } else {
                    break
                }
            }
        }
        return destination
    }

    @ExperimentalCoroutinesApi
    suspend fun runScheduler() = coroutineScope {
        try {
            while (isActive) {
                LOG.trace("Peeking Q")
                val next = mut.withLock { priorityQueue.firstOrNull() }
                next?.let { it ->
                    notifyChannel.poll() //clear notification
                    LOG.info("Next task at ${it.first}")
                    val timeToTask = now().timeToTaskMillis(it.first)
                    val scope = this
                    timeToTask?.let {
                        select<Any> {
                            notifyChannel.onReceive { }
                            onTimeout(timeToTask) { handleTasks(scope) }
                        }
                    }
                    LOG.trace("End of select")
                } ?: run {
                    //null next time, wait for a notification
                    LOG.trace("Empty task queue")
                    notifyChannel.receive()
                }
                LOG.trace("End of while")
            }
        } finally {
            mut.withLock { priorityQueue.clear() }
        }
        LOG.info("Scheduler finish")
    }

    private suspend fun handleTasks(coroutineScope: CoroutineScope) {
        mut.withLock {
            val now = now()
            priorityQueue.removeWhile { task -> task.first?.let { t -> t < now } ?: false }
                .forEach { pair ->
                    coroutineScope.launch {
                        try {
                            pair.second.second()
                        } catch (e: Exception) {
                            this@Scheduler.LOG.error("Error running task", e)
                        }
                        this@Scheduler.addToSchedule(pair.second)
                        this@Scheduler.LOG.info("Task finish. Re-added to queue")
                    }
                }
        }

    }
}