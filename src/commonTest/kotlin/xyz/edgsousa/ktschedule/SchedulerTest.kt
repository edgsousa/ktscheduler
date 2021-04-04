package xyz.edgsousa.ktschedule

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals


class SchedulerTest {

    class MyTask {
        var i = 0

        fun dosomething() {
            println("${now()}: TASK I am")
            i += 1
        }

    }

    @ExperimentalCoroutinesApi
    @Test
    fun testit() = runTest {
        val now = (now().plus(1000, ChronoUnit.MILLIS) as Instant).atZone(systemDefault()).toLocalDateTime()
        val tod = TimeOfDay(now.toLocalTime())
        val repeat = RepeatingTime(now.toLocalTime(), 1, ChronoUnit.SECONDS)
        println("${now()}: Tasks will run at ${tod.time} local time")
        val tSingle = MyTask()
        val tMultiple = MyTask()

        val job = launch { Scheduler.runScheduler() }
        Scheduler.addToSchedule(Task(repeat) { tMultiple.dosomething() })
        Scheduler.addToSchedule(Task(tod) { tSingle.dosomething() })

        assertEquals(0, tSingle.i)
        assertEquals(0, tMultiple.i)
        println("${now()}: waiting for task??")

        delay(500)
        println("${now()}: checking result not 1 ")
        assertNotEquals(1, tSingle.i)
        assertNotEquals(1, tMultiple.i)


        delay(600)
        println("${now()}: checking result 1 ")
        assertEquals(1, tSingle.i)
        assertEquals(1, tMultiple.i)

        delay(1100)
        println("${now()}: checking result n ")
        assertEquals(1, tSingle.i)
        assertEquals(2, tMultiple.i)

        delay(1100)
        println("${now()}: checking result n ")
        assertEquals(1, tSingle.i)
        assertEquals(3, tMultiple.i)
        job.cancel()
    }

    @ExperimentalCoroutinesApi
    @Test fun longTask() = runTest {
        val now = (now().plus(1000, ChronoUnit.MILLIS) as Instant).atZone(systemDefault()).toLocalDateTime()
        val repeat = RepeatingTime(now.toLocalTime(), 1, ChronoUnit.SECONDS)

        println("${now()}: Scheduling task")
        Scheduler.addToSchedule(Task(repeat) {
            println("${now()}: Started task")
            val target = now().plus(10, ChronoUnit.SECONDS) as Instant
            while(now() < target) {
                //nothing
            }
            println("${now()}: Finished task")
        })

        val job = launch { Scheduler.runScheduler() }

        delay(25000)
        job.cancel()
    }
}