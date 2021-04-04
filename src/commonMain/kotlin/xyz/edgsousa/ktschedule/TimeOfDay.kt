package xyz.edgsousa.ktschedule

/**
 * Simple time of day expressed in local time
 * Suitable for everyday tasks
 */
class TimeOfDay(val time: LocalTime) : PointInTime {

    /**
     * Time later today, or tomorrow
     */
    override fun next(instant: Instant?): Instant {
        val zone = systemDefault()
        return (instant ?: now()).let {
            val datetime = ofInstant(it, zone)
            val nextTime = ofDateTime(datetime.toLocalDate(), time)
            fromZoned(
                (if (nextTime.isAfter(datetime)) {
                    nextTime
                } else {
                    nextTime.plus(1, ChronoUnit.DAYS)
                }).atZone(zone)
            )
        }
    }

    /**
     * Time yesterday, or today until now
     */
    override fun previous(instant: Instant?): Instant {
        val zone = systemDefault()
        return (instant ?: now()).let {
            val datetime = ofInstant(it, zone)
            val previousTime = ofDateTime(datetime.toLocalDate(), time)
            fromZoned(
                (if (!previousTime.isAfter(datetime)) {
                    previousTime
                } else {
                    previousTime.plus(-1, ChronoUnit.DAYS)
                }).atZone(zone)
            )
        }
    }
}