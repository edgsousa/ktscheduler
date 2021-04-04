package xyz.edgsousa.ktschedule

/**
 * Times during a day, doesn't return yesterday/tomorrow's values
 */
class TimesInDay(vararg times: LocalTime): PointInTime {
    constructor(repeatingTime: RepeatingTime): this(*repeatingTime.resolveLocalTimesList().toTypedArray())
    private val sortedTimes = times.sorted()

    override fun next(instant: Instant?): Instant? {
        val nowLocalDate = (instant?:now()).atZone(systemDefault()).toLocalDateTime()
        val nowTime = nowLocalDate.toLocalTime()
        val idx = sortedTimes.binarySearch(nowTime).let { if(it >= 0) it+1 else -(it+1)}
        return sortedTimes.elementAtOrNull(idx)
            ?.let { fromZoned(ofDateTime(nowLocalDate.toLocalDate(), it).atZone(systemDefault())) }
    }

    override fun previous(instant: Instant?): Instant? {
        val nowLocalDate = (instant?:now()).atZone(systemDefault()).toLocalDateTime()
        val nowTime = nowLocalDate.toLocalTime()
        val idx = sortedTimes.binarySearch(nowTime).let { if(it >= 0) it else -(it+1)-1}
        return sortedTimes.elementAtOrNull(idx)
            ?.let { fromZoned(ofDateTime(nowLocalDate.toLocalDate(), it).atZone(systemDefault())) }
    }

}