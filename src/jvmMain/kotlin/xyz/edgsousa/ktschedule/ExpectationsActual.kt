package xyz.edgsousa.ktschedule

import kotlinx.coroutines.runBlocking
import java.time.DayOfWeek
import java.time.chrono.ChronoLocalDate
import java.time.chrono.ChronoLocalDateTime
import java.time.chrono.ChronoZonedDateTime
import java.time.temporal.*
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import java.time.temporal.Temporal
import java.time.temporal.TemporalAdjuster
import java.time.temporal.TemporalField
import java.time.temporal.TemporalUnit
import kotlin.reflect.KClass
import java.time.Instant as jInstant
import java.time.LocalDate as jLocalDate
import java.time.LocalDateTime as jLocalDateTime
import java.time.LocalTime as jLocalTime
import java.time.ZoneId as jZoneId
import java.time.ZonedDateTime as jZonedDateTime

actual typealias Instant = jInstant

actual typealias LocalTime = jLocalTime
actual typealias LocalDate = jLocalDate
actual typealias LocalDateTime = jLocalDateTime
actual typealias ZoneId = jZoneId

actual fun now(): Instant {
    return jInstant.now()
}

actual fun systemDefault(): ZoneId {
    return jZoneId.systemDefault()
}

actual typealias ZonedDateTime = jZonedDateTime

actual fun ofInstant(instant: Instant, zone: ZoneId) = jLocalDateTime.ofInstant(instant, zone)

actual fun ofDateTime(date: LocalDate, time: LocalTime) = jLocalDateTime.of(date, time)

actual fun fromZoned(zonedDateTime: ChronoZonedDateTime<*>): Instant = jInstant.from(zonedDateTime)

actual typealias TemporalUnit = TemporalUnit

/**
 * Follow the Java 1.8 time API
 */
actual typealias Temporal = Temporal
actual typealias TemporalField = TemporalField
actual typealias TemporalAdjuster = TemporalAdjuster
actual typealias ChronoLocalDate = ChronoLocalDate
actual typealias ChronoZonedDateTime<D> = ChronoZonedDateTime<D>
actual typealias ChronoLocalDateTime<D> = ChronoLocalDateTime<D>
actual typealias ChronoField = ChronoField
actual typealias ChronoUnit = ChronoUnit

actual typealias DayOfWeek = DayOfWeek

actual fun LocalTimeOf(hour: Int, minute: Int, second: Int) = jLocalTime.of(hour, minute, second)
actual fun LocalDateOf(year: Int, month: Int, day: Int) = jLocalDate.of(year, month, day)
actual fun ofZoneId(zone: String) = jZoneId.of(zone)

actual fun next(day: DayOfWeek) = TemporalAdjusters.next(day)
actual fun nextOrSame(day: DayOfWeek)= TemporalAdjusters.nextOrSame(day)
actual fun previous(day: DayOfWeek)= TemporalAdjusters.previous(day)
actual fun previousOrSame(day: DayOfWeek)= TemporalAdjusters.previousOrSame(day)

/****************/

actual typealias Logger = org.slf4j.Logger

actual class LoggerFactory {
    actual companion object {
        actual fun getLogger(clz: KClass<*>): Logger = org.slf4j.LoggerFactory.getLogger(clz.java)
    }
}