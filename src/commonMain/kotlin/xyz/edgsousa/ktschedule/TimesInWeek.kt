package xyz.edgsousa.ktschedule

import xyz.edgsousa.ktschedule.next as nextInWeek
import xyz.edgsousa.ktschedule.previous as previousInWeek

/**
 * Each chosen day of week might have a different set of times
 */
class TimesInWeek(val times: Map<DayOfWeek, TimesInDay>) : PointInTime {

    /**
     * Each chosen day of week has the same set of times
     */
    constructor(timesInDay: TimesInDay, daysOfWeek: Set<DayOfWeek>) :
            this(daysOfWeek.map { it to timesInDay }.toMap())

    constructor(repeatingTime: RepeatingTime, weekDays: Set<DayOfWeek>) :
            this(TimesInDay(repeatingTime), weekDays)

    override fun next(instant: Instant?): Instant? {
        val z = systemDefault()
        val now = instant ?: now()
        val nowLocalDate = now.atZone(z).toLocalDateTime()

        return times.map {
            if (it.key == nowLocalDate.getDayOfWeek()) {
                it.value.next(now)
            } else {
                val nextDate = fromZoned((nowLocalDate.with(nextInWeek(it.key)) as LocalDateTime).atZone(z))
                it.value.next(todaysMidnight(nextDate))
            }
        }.filterNotNull()
            .minByOrNull { it }
    }

    override fun previous(instant: Instant?): Instant? {
        val z = systemDefault()
        val now = instant ?: now()
        val nowLocalDate = now.atZone(z).toLocalDateTime()

        val results = times.map {
            if (it.key == nowLocalDate.getDayOfWeek()) {
                it.value.previous(now)
            } else {
                val nextDate = fromZoned((nowLocalDate.with(previousInWeek(it.key)) as LocalDateTime).atZone(z))
                it.value.previous(almostMidnight(nextDate))
            }
        }
        results.forEachIndexed { i, it ->
            println("$i -> $it")
        }
        return results.filterNotNull()
            .maxByOrNull { it }
    }
}
