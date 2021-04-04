package xyz.edgsousa.ktschedule

import kotlin.test.Test
import kotlin.test.assertEquals

internal class TimesInWeekTest: AbstractTimesTest() {

    @Test
    fun findTodaysTimes() {
        val t = TimesInWeek(RepeatingTime(LocalTimeOf(12, 0), 30, ChronoUnit.MINUTES), setOf(*DayOfWeek.values()))

        val before = fromZoned(ofDateTime(d.toLocalDate(), LocalTimeOf(13, 0)).atZone(z))
        val now = fromZoned(ofDateTime(d.toLocalDate(), LocalTimeOf(13, 20)).atZone(z))
        val after = fromZoned(ofDateTime(d.toLocalDate(), LocalTimeOf(13, 30)).atZone(z))


        assertEquals(before, t.previous(now))
        assertEquals(after, t.next(now))

        val lastToday = fromZoned(ofDateTime(d.toLocalDate(), LocalTimeOf(23, 30)).atZone(z))

        val yesterday = lastToday.plus(-1, ChronoUnit.DAYS) as Instant
        val morning = fromZoned(ofDateTime(d.toLocalDate(), LocalTimeOf(11, 20)).atZone(z))
        val today = fromZoned(ofDateTime(d.toLocalDate(), LocalTimeOf(12, 0)).atZone(z))

        assertEquals(yesterday, t.previous(morning))
        assertEquals(today, t.next(morning))


        val lateNight = fromZoned(ofDateTime(d.toLocalDate(), LocalTimeOf(23, 40)).atZone(z))
        val tomorrow = today.plus(1, ChronoUnit.DAYS) as Instant

        assertEquals(lastToday, t.previous(lateNight))
        assertEquals(tomorrow, t.next(lateNight))
    }
}