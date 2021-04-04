package xyz.edgsousa.ktschedule

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

open class RepeatingTimeTest: AbstractTimesTest() {

    @Test fun repeatTest() {
        val time = LocalTimeOf(12, 30)
        val dateTime = fromZoned(ofDateTime(d.toLocalDate(), time).atZone(z))

        val expectBefore = fromZoned(ofDateTime(d.toLocalDate(), LocalTimeOf(12, 55)).atZone(z))
        val nowDate = fromZoned(ofDateTime(d.toLocalDate(), LocalTimeOf(13, 0)).atZone(z))
        val expectAfter = fromZoned(ofDateTime(d.toLocalDate(), LocalTimeOf(13, 20)).atZone(z))

        val morning = fromZoned(ofDateTime(d.toLocalDate(), LocalTimeOf(12, 0)).atZone(z))

        //12:30 .... 17:30...22:30....23:45
        val lastAtNight = fromZoned(ofDateTime(d.toLocalDate(), LocalTimeOf(23, 45)).atZone(z))
        val lateNight = fromZoned(ofDateTime(d.toLocalDate(), LocalTimeOf(23, 59)).atZone(z))
        val t = RepeatingTime(time, 25, ChronoUnit.MINUTES)


        assertEquals(expectBefore, t.previous(nowDate))
        assertEquals(expectAfter, t.next(nowDate))

        assertNull(t.previous(morning))
        assertEquals(dateTime, t.next(morning))

        assertEquals(lastAtNight, t.previous(lateNight))
        assertNull(t.next(lateNight))

        val previousDay = lastAtNight.plus(-1, ChronoUnit.DAYS) as Instant
        val nextDay = dateTime.plus(1, ChronoUnit.DAYS) as Instant

        assertEquals(previousDay, t.previousRepeating(morning))
        assertEquals(nextDay, t.nextRepeating(lateNight))

    }
}