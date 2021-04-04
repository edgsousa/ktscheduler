package xyz.edgsousa.ktschedule

/**
 * Repeating a time, from startTime until midnight, every `each` `unit`s
 */
class RepeatingTime(private val startTime: LocalTime, private val each: Long, private val unit: TemporalUnit): PointInTime {
    override fun next(instant: Instant?): Instant? {
        val now = instant?: now()
        val nowDate = now.atZone(systemDefault()).toLocalDateTime()

        val amountToAdd = if(startTime.isBefore(nowDate.toLocalTime())) {
            val diff = unit.between(startTime, nowDate.toLocalTime())
            diff + each - (diff % each)
        } else {
            0
        }
        val nextTime = ofDateTime(nowDate.toLocalDate(), startTime).plus(amountToAdd, unit)
        return if(nextTime.toLocalDate().isAfter(nowDate.toLocalDate())) {
            //if next run is next day, null
            null
        } else {
            fromZoned(nextTime.atZone(systemDefault()))
        }

    }

    override fun previous(instant: Instant?): Instant? {
        val now = instant?: now()
        val nowDate = now.atZone(systemDefault()).toLocalDateTime()
        if (startTime.isAfter(nowDate.toLocalTime())) {
            //previous time was yesterday?
            return null
        }

        val diff = unit.between(startTime, nowDate.toLocalTime())
        val amountToAdd = diff - (diff % each)
        return fromZoned(ofDateTime(nowDate.toLocalDate(), startTime).plus(amountToAdd, unit).atZone(systemDefault()))
    }

    fun resolveLocalTimesList(): List<LocalTime> {
        val res = mutableListOf<LocalTime>()
        val date = LocalDateOf(1, 1, 1)
        var dateTime = ofDateTime(date, startTime)
        while (!date.isBefore(dateTime.toLocalDate())) {
            res.add(dateTime.toLocalTime())
            dateTime = dateTime.plus(each, unit) as LocalDateTime
        }
        return res
    }
}