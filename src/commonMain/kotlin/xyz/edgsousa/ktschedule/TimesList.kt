package xyz.edgsousa.ktschedule

/**
 * Join of specific (local) dates and times with optional PointInTime structures for one-off usages
 * in task's scheduling timetables
 */
class TimesList(vararg times: LocalDateTime, private val otherTimes: PointInTime? = null) : PointInTime {
    private val sortedTimes = times.sorted()

    override fun next(instant: Instant?): Instant? {
        val now = instant ?: now()
        val z = systemDefault()
        val nextToday = otherTimes?.next(now)
        val fromTimeList = sortedTimes.map { fromZoned(it.atZone(z)) }.sorted().firstOrNull { (it > now) }

        return when {
            nextToday == null && fromTimeList == null -> null
            nextToday == null -> fromTimeList
            fromTimeList == null -> nextToday
            fromTimeList < nextToday -> fromTimeList
            else -> nextToday
        }

    }

    override fun previous(instant: Instant?): Instant? {
        val now = instant ?: now()
        val z = systemDefault()
        val nextToday = otherTimes?.previous(now)
        val fromTimeList = sortedTimes.map { fromZoned(it.atZone(z)) }.sorted().lastOrNull { it <= now }

        return when {
            nextToday == null && fromTimeList == null -> null
            nextToday == null -> fromTimeList
            fromTimeList == null -> nextToday
            fromTimeList > nextToday -> fromTimeList
            else -> nextToday
        }
    }

}