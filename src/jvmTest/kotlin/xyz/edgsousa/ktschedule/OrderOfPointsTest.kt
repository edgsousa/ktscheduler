package xyz.edgsousa.ktschedule

import io.mockk.every
import io.mockk.mockkStatic
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class OrderOfPointsTest: AbstractTimesTest() {

    @Test
    fun testOrdering() {
        val now1 = Instant.from(LocalDateTime.of(LocalDate.now(), LocalTime.of(12,0)).atZone(ZoneId.systemDefault()))
        val now2 = Instant.from(LocalDateTime.of(LocalDate.now(), LocalTime.of(10,0)).atZone(ZoneId.systemDefault()))
        val now3  = Instant.from(LocalDateTime.of(LocalDate.now(), LocalTime.of(13,30)).atZone(ZoneId.systemDefault()))
        val now4  =  Instant.from(LocalDateTime.of(LocalDate.now(), LocalTime.of(23,30)).atZone(ZoneId.systemDefault()))

        val t1 = TimeOfDay(LocalTime.of(13, 0))
        val t2 = RepeatingTime(LocalTime.of(11,0), 3, ChronoUnit.HOURS)

        mockkStatic("xyz.edgsousa.ktschedule.ExpectationsActualKt")

        //at 12:00 -> next will be t1
        every { now() } returns now1
        assertTrue(t1 < t2)

        //at 10 am, next will be t2
        every { now() } returns now2
        assertTrue(t2 < t1)

        //after t1 has passed, a repetition of t2
        every { now() } returns now3
        assertTrue(t2 < t1)

        //next day, first will be t2
        every { now() } returns now4
        assertTrue(t2 < t1)

        //t3 will be next day, after t2
        val t3 = TimeOfDay(LocalTimeOf(14, 0))
        assertTrue(t2 < t3)

        //next will be both t2 and t3
        every { now() } returns now3

        assertFalse(t2 < t3)
        assertFalse(t2 > t3)
        assertEquals(0, t2.compareTo(t3))
    }
}