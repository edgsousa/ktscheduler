package xyz.edgsousa.ktschedule

import kotlin.test.Test
import kotlin.test.assertEquals

internal class TimeOfDayTest: AbstractTimesTest() {

    @Test
    fun testNextAndPrevious() {
        val time = LocalTimeOf(12, 30)
        val yesterday = fromZoned(ofDateTime(d.plus(-1, ChronoUnit.DAYS).toLocalDate(), time).atZone(z))
        val tomorrow = fromZoned(ofDateTime(d.plus(1, ChronoUnit.DAYS).toLocalDate(), time).atZone(z))
        val t = TimeOfDay(time)
        val today = fromZoned(ofDateTime(d.toLocalDate(), time).atZone(z))
        val morning = fromZoned(ofDateTime(d.toLocalDate(), LocalTimeOf(11, 30)).atZone(z))
        val afternoon = fromZoned(ofDateTime(d.toLocalDate(), LocalTimeOf(13, 30)).atZone(z))


        assertEquals(tomorrow, t.next(today))
        assertEquals(today, t.previous(today))

        assertEquals(today, t.next(morning))
        assertEquals(yesterday, t.previous(morning))

        assertEquals(tomorrow, t.next(afternoon))
        assertEquals(today, t.previous(afternoon))

    }
}