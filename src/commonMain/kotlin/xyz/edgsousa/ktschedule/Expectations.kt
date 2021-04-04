package xyz.edgsousa.ktschedule

import kotlin.reflect.KClass


/**
 * Follow the Java 1.8 time API
 */
expect interface Temporal {
    fun plus(amountToAdd: Long, unit: TemporalUnit): Temporal
    open fun with(adjuster: TemporalAdjuster): Temporal
    fun with(field: TemporalField, value: Long): Temporal
    open fun get(field: TemporalField): Int
}

expect interface TemporalAdjuster {
    fun adjustInto(temporal: Temporal): Temporal
}

expect interface TemporalField

expect enum class ChronoField: TemporalField {
    DAY_OF_WEEK,
    EPOCH_DAY,
    NANO_OF_DAY
}

expect fun next(day: DayOfWeek): TemporalAdjuster
expect fun nextOrSame(day: DayOfWeek): TemporalAdjuster
expect fun previous(day: DayOfWeek): TemporalAdjuster
expect fun previousOrSame(day: DayOfWeek): TemporalAdjuster

expect interface ChronoLocalDate : Temporal, Comparable<ChronoLocalDate> {
    open fun isAfter(other: ChronoLocalDate): Boolean
    open fun isBefore(other: ChronoLocalDate): Boolean
}

expect interface ChronoZonedDateTime<D : ChronoLocalDate> : Temporal

expect interface ChronoLocalDateTime<D : ChronoLocalDate> : Temporal, Comparable<ChronoLocalDateTime<*>>, TemporalAdjuster {
    fun toLocalDate(): D
    fun toLocalTime(): LocalTime
    open fun isAfter(other: ChronoLocalDateTime<*>): Boolean
    open fun isBefore(other: ChronoLocalDateTime<*>): Boolean
    fun atZone(zone: ZoneId): ChronoZonedDateTime<D>
    override fun plus(amountToAdd: Long, unit: TemporalUnit): ChronoLocalDateTime<D>
}

expect class Instant : Comparable<Instant>, Temporal, TemporalAdjuster {
    fun atZone(zone: ZoneId): ZonedDateTime
}

expect fun now(): Instant

expect class LocalTime : Temporal, Comparable<LocalTime>, TemporalAdjuster {
    fun isAfter(other: LocalTime): Boolean
    fun isBefore(other: LocalTime): Boolean
}

expect fun LocalTimeOf(hour: Int, minute: Int, second: Int = 0): LocalTime

expect class LocalDate : ChronoLocalDate, TemporalAdjuster

expect fun LocalDateOf(year: Int, month: Int, day: Int): LocalDate

expect class LocalDateTime : ChronoLocalDateTime<LocalDate>, TemporalAdjuster {
    fun getDayOfWeek(): DayOfWeek
}

expect interface TemporalUnit {
    fun between(temporal1Inclusive: Temporal, temporal2Exclusive: Temporal): Long
}

expect enum class ChronoUnit : TemporalUnit {
    DAYS,
    HOURS,
    MINUTES,
    NANOS,
    SECONDS,
    MILLIS
}

expect enum class DayOfWeek: TemporalAdjuster {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY
}

expect fun ofInstant(instant: Instant, zone: ZoneId): LocalDateTime
expect fun ofDateTime(date: LocalDate, time: LocalTime): LocalDateTime
expect fun fromZoned(zonedDateTime: ChronoZonedDateTime<*>): Instant

expect class ZonedDateTime {
    fun toLocalDateTime(): LocalDateTime
}

expect abstract class ZoneId

expect fun systemDefault(): ZoneId

expect fun ofZoneId(zone: String): ZoneId

/***********************/
expect class LoggerFactory {
    companion object {
        fun getLogger(clz: KClass<*>): Logger
    }
}

expect interface Logger {
    fun info(msg: String, vararg objects: Any)
    fun debug(msg: String, vararg objects: Any)
    fun warn(msg: String, vararg objects: Any)
    fun error(msg: String, vararg objects: Any)
    fun trace(msg: String, vararg objects: Any)
}
