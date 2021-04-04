package xyz.edgsousa.ktschedule

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class TimesInDayTest: AbstractTimesTest() {

    @Test fun timesInDayList() {
        val time = LocalTimeOf(12, 30)
        val now = fromZoned(ofDateTime(d.toLocalDate(), time).atZone(z))
        val t = TimesInDay(RepeatingTime(LocalTimeOf(12, 0), 1, ChronoUnit.HOURS))

        val beforeNoon = fromZoned(ofDateTime(d.toLocalDate(), LocalTimeOf(11, 59)).atZone(z))
        val noon = fromZoned(ofDateTime(d.toLocalDate(), LocalTimeOf(12, 0)).atZone(z))
        val _23h = fromZoned(ofDateTime(d.toLocalDate(), LocalTimeOf(23, 0)).atZone(z))
        val _13h = fromZoned(ofDateTime(d.toLocalDate(), LocalTimeOf(13, 0)).atZone(z))


        assertNull(t.previous(beforeNoon))
        assertEquals(noon, t.next(beforeNoon))

        assertEquals(_13h, t.next(now))
        assertEquals(noon, t.previous(now))

        assertEquals(_13h, t.next(noon))
        assertEquals(noon, t.previous(noon))

        assertNull(t.next(_23h))
        assertEquals(_23h, t.previous(_23h))

    }

    @Test fun timesWithOneShot() {
        val time = LocalTimeOf(12, 30)
        val now = fromZoned(ofDateTime(d.toLocalDate(), time).atZone(z))
        val t = TimesInDay(RepeatingTime(LocalTimeOf(12, 0), 1, ChronoUnit.HOURS))

        val t2 = TimesList(ofDateTime(d.toLocalDate(), time), otherTimes = t)

        assertEquals(now, t2.next(now.plus(-1, ChronoUnit.NANOS) as Instant))
        assertEquals(now, t2.previous(now.plus(1, ChronoUnit.NANOS) as Instant))
    }
}