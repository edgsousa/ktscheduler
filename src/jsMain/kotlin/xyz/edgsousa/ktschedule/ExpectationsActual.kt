package xyz.edgsousa.ktschedule
import JSJoda.Duration
import JSJoda.ZoneOffset
import kotlin.math.log
import kotlin.reflect.KClass

/**
 * Follow the Java 1.8 time API
 */
actual interface Temporal {
    val joda: JSJoda.Temporal
    actual fun plus(amountToAdd: Long, unit: TemporalUnit): Temporal
    fun minus(amountToSubtract: Long, unit: TemporalUnit) = plus(-amountToSubtract, unit)

    actual fun with(adjuster: TemporalAdjuster): Temporal {
        return adjuster.adjustInto(this)
    }

    actual fun with(field: TemporalField, value: Long): Temporal
    actual fun get(field: TemporalField): Int = joda.get(field.joda).toInt()

    override fun equals(other: Any?): Boolean

    override fun toString(): String
}


actual interface TemporalField {
    val joda: JSJoda.TemporalField
}

actual enum class ChronoField(override val joda: JSJoda.ChronoField) : TemporalField {
    DAY_OF_WEEK(JSJoda.ChronoField.DAY_OF_WEEK),
    EPOCH_DAY(JSJoda.ChronoField.EPOCH_DAY),
    NANO_OF_DAY(JSJoda.ChronoField.NANO_OF_DAY);

    companion object {
        val map = values().associateBy(ChronoField::joda)
        fun fromJoda(value: JSJoda.ChronoField) = map[value]!!
    }
}

actual fun next(day: DayOfWeek): TemporalAdjuster {
    val doW = day.ordinal + 1
    return object : TemporalAdjuster {
        override fun adjustInto(it: Temporal): Temporal {
            val calDoW = it.get(ChronoField.DAY_OF_WEEK)
            val daysDiff = calDoW - doW
            return it.plus((if (daysDiff >= 0) 7 - daysDiff else -daysDiff).toLong(), ChronoUnit.DAYS)
        }
    }
}

actual fun nextOrSame(day: DayOfWeek): TemporalAdjuster {
    val doW = day.ordinal + 1
    return object : TemporalAdjuster {
        override fun adjustInto(it: Temporal): Temporal {
            val calDoW = it.get(ChronoField.DAY_OF_WEEK)
            if (calDoW == doW) {
                return it
            }
            val daysDiff = calDoW - doW
            return it.plus((if (daysDiff >= 0) 7 - daysDiff else -daysDiff).toLong(), ChronoUnit.DAYS)
        }
    }
}

actual fun previous(day: DayOfWeek): TemporalAdjuster {
    val doW = day.ordinal + 1
    return object : TemporalAdjuster {
        override fun adjustInto(it: Temporal): Temporal {
            val calDoW = it.get(ChronoField.DAY_OF_WEEK)
            val daysDiff = doW - calDoW
            return it.minus((if (daysDiff >= 0) 7 - daysDiff else -daysDiff).toLong(), ChronoUnit.DAYS)
        }
    }
}

actual fun previousOrSame(day: DayOfWeek): TemporalAdjuster {
    val doW = day.ordinal + 1
    return object : TemporalAdjuster {
        override fun adjustInto(it: Temporal): Temporal {
            val calDoW = it.get(ChronoField.DAY_OF_WEEK)
            if (calDoW == doW) {
                return it
            }
            val daysDiff = doW - calDoW
            return it.minus((if (daysDiff >= 0) 7 - daysDiff else -daysDiff).toLong(), ChronoUnit.DAYS)
        }
    }
}

actual interface ChronoLocalDate : Temporal, Comparable<ChronoLocalDate> {
    actual fun isAfter(other: ChronoLocalDate): Boolean {
        return this.get(ChronoField.EPOCH_DAY) > other.get(ChronoField.EPOCH_DAY)
    }

    actual fun isBefore(other: ChronoLocalDate): Boolean {
        return this.get(ChronoField.EPOCH_DAY) < other.get(ChronoField.EPOCH_DAY)
    }

}

actual interface ChronoZonedDateTime<D : ChronoLocalDate> : Temporal {
    override val joda: JSJoda.ChronoZonedDateTime
}


actual class Instant(override val joda: JSJoda.Instant) : Comparable<Instant>, Temporal, TemporalAdjuster {

    actual fun atZone(zone: ZoneId): ZonedDateTime {
        return ZonedDateTime(joda.atZone((zone as CZoneId).joda))
    }

    override fun compareTo(other: Instant): Int {
        return joda.compareTo(other.joda).toInt()
    }

    override fun plus(amountToAdd: Long, unit: TemporalUnit): Temporal {
        return Instant(joda.plus(amountToAdd.toInt(), unit.joda)) //to int, otherwise overflow exception
    }

    override fun with(field: TemporalField, value: Long): Temporal {
        return Instant(joda.with((field as ChronoField).joda.unsafeCast<JSJoda.TemporalField>(), value))
    }

    override fun equals(other: Any?): Boolean {
        return (other is Temporal) && joda.equals(other.joda)
    }

    override fun toString(): String {
        return joda.toString()
    }

    override fun adjustInto(temporal: Temporal): Temporal {
        val x = joda.adjustInto(temporal.joda)
        val duration = Duration.between(JSJoda.Instant.EPOCH, x)
        return Instant(JSJoda.Instant.EPOCH.plus(duration))
    }
}


actual fun now(): Instant {
    return Instant(JSJoda.Instant.now())
}

actual fun LocalTimeOf(hour: Int, minute: Int, second: Int): LocalTime {
    return LocalTime(JSJoda.LocalTime.of(hour, minute, second, 0))
}

actual class LocalDate(override val joda: JSJoda.LocalDate) : ChronoLocalDate, TemporalAdjuster {
    override fun compareTo(other: ChronoLocalDate): Int {
        return this.joda.compareTo((other as LocalDate).joda).toInt()
    }

    override fun plus(amountToAdd: Long, unit: TemporalUnit): LocalDate {
        return LocalDate(joda.plus(amountToAdd, unit.joda))
    }

    override fun with(field: TemporalField, value: Long): LocalDate {
        return LocalDate(joda.with((field as ChronoField).joda.unsafeCast<JSJoda.TemporalField>(), value))
    }

    override fun equals(other: Any?): Boolean  {
        return (other is Temporal) && joda.equals(other.joda)
    }

    override fun toString(): String {
        return joda.toString()
    }

    override fun adjustInto(temporal: Temporal): Temporal {
        return temporal.with(ChronoField.EPOCH_DAY, get(ChronoField.EPOCH_DAY).toLong())
    }
}

actual fun LocalDateOf(year: Int, month: Int, day: Int): LocalDate {
    return LocalDate(JSJoda.LocalDate.of(year, month, day))
}

actual interface TemporalUnit {
    val joda: JSJoda.TemporalUnit
    actual fun between(
        temporal1Inclusive: Temporal,
        temporal2Exclusive: Temporal
    ): Long
}

actual enum class ChronoUnit(override val joda: JSJoda.TemporalUnit) : TemporalUnit {
    DAYS(JSJoda.ChronoUnit.DAYS),
    HOURS(JSJoda.ChronoUnit.HOURS),
    MINUTES(JSJoda.ChronoUnit.MINUTES),
    SECONDS(JSJoda.ChronoUnit.SECONDS),
    MILLIS(JSJoda.ChronoUnit.MILLIS),
    NANOS(JSJoda.ChronoUnit.NANOS);

    override fun between(
        temporal1Inclusive: Temporal,
        temporal2Exclusive: Temporal
    ): Long = joda.between(temporal1Inclusive.joda, temporal2Exclusive.joda).toLong()
}

actual enum class DayOfWeek(val joda: JSJoda.DayOfWeek) : TemporalAdjuster {
    MONDAY(JSJoda.DayOfWeek.MONDAY),
    TUESDAY(JSJoda.DayOfWeek.TUESDAY),
    WEDNESDAY(JSJoda.DayOfWeek.WEDNESDAY),
    THURSDAY(JSJoda.DayOfWeek.THURSDAY),
    FRIDAY(JSJoda.DayOfWeek.FRIDAY),
    SATURDAY(JSJoda.DayOfWeek.SATURDAY),
    SUNDAY(JSJoda.DayOfWeek.SUNDAY);

    override fun adjustInto(temporal: Temporal): Temporal {
        return temporal.with(ChronoField.DAY_OF_WEEK, this.ordinal + 1L);
    }

    companion object {
        val map = values().associateBy(DayOfWeek::joda)
        fun fromJoda(value: JSJoda.DayOfWeek) = map[value]!!
    }
}

actual fun ofInstant(
    instant: Instant,
    zone: ZoneId
): LocalDateTime {
    return LocalDateTime(instant.joda.atZone((zone as CZoneId).joda).toLocalDateTime())
}

actual fun ofDateTime(
    date: LocalDate,
    time: LocalTime
): LocalDateTime {
    return LocalDateTime(JSJoda.LocalDateTime.of(date.joda, time.joda))
}

actual class ZonedDateTime(override val joda: JSJoda.ZonedDateTime) : ChronoZonedDateTime<LocalDate> {
    actual fun toLocalDateTime(): LocalDateTime {
        return LocalDateTime(joda.toLocalDateTime())
    }

    override fun plus(amountToAdd: Long, unit: TemporalUnit): Temporal {
        return ZonedDateTime(joda.plus(amountToAdd, unit.joda))
    }

    override fun with(field: TemporalField, value: Long): Temporal {
        return ZonedDateTime(joda.with((field as ChronoField).joda.unsafeCast<JSJoda.TemporalField>(), value))
    }

    override fun equals(other: Any?): Boolean {
        return (other is Temporal) && joda.equals(other.joda)
    }

    override fun toString(): String {
        return joda.toString()
    }
}

actual abstract class ZoneId

class CZoneId(val joda: JSJoda.ZoneId) : ZoneId()

actual fun systemDefault(): ZoneId {
    return CZoneId(JSJoda.ZoneId.SYSTEM)
}

actual fun ofZoneId(zone: String): ZoneId {
    return CZoneId(JSJoda.ZoneId.of(zone))
}

actual fun fromZoned(zonedDateTime: ChronoZonedDateTime<*>): Instant {
    return Instant(zonedDateTime.joda.toInstant())
}


actual class LocalDateTime(override val joda: JSJoda.LocalDateTime) : ChronoLocalDateTime<LocalDate>, TemporalAdjuster {
    actual fun getDayOfWeek(): DayOfWeek {
        return DayOfWeek.fromJoda(joda.dayOfWeek())
    }

    override fun with(field: TemporalField, value: Long): Temporal {
        return LocalDateTime(joda.with((field as ChronoField).joda.unsafeCast<JSJoda.TemporalField>(), value))
    }

    override fun equals(other: Any?): Boolean {
        return (other is Temporal) && joda.equals(other.joda)
    }

    override fun toString(): String {
        return joda.toString()
    }

    override fun compareTo(other: ChronoLocalDateTime<*>): Int {
        return this.joda.compareTo((other as LocalDateTime).joda).toInt()
    }

    override fun toLocalDate(): LocalDate {
        return LocalDate(joda.toLocalDate())
    }

    override fun toLocalTime(): LocalTime {
        return LocalTime(joda.toLocalTime())
    }

    override fun atZone(zone: ZoneId): ChronoZonedDateTime<LocalDate> {
        return ZonedDateTime(joda.atZone((zone as CZoneId).joda))
    }

    override fun plus(amountToAdd: Long, unit: TemporalUnit): ChronoLocalDateTime<LocalDate> {
        return LocalDateTime(joda.plus(amountToAdd, unit.joda))
    }

    override fun adjustInto(temporal: Temporal): Temporal {
        return temporal
            .with(ChronoField.EPOCH_DAY, joda.toLocalDate().toEpochDay().toLong())
            .with(ChronoField.NANO_OF_DAY, joda.toLocalTime().toNanoOfDay().toLong())
    }
}

actual class LocalTime(override val joda: JSJoda.LocalTime) : Temporal, Comparable<LocalTime>, TemporalAdjuster {
    actual fun isAfter(other: LocalTime): Boolean {
        return joda.isAfter(other.joda)
    }

    actual fun isBefore(other: LocalTime): Boolean {
        return joda.isBefore(other.joda)
    }

    override fun compareTo(other: LocalTime): Int {
        return joda.compareTo(other.joda).toInt()
    }

    override fun plus(amountToAdd: Long, unit: TemporalUnit): Temporal {
        return LocalTime(joda.plus(amountToAdd, unit.joda))
    }

    override fun with(field: TemporalField, value: Long): Temporal {
        return LocalTime(joda.with((field as ChronoField).joda.unsafeCast<JSJoda.TemporalField>(), value))
    }

    override fun equals(other: Any?): Boolean {
        return (other is Temporal) && joda.equals(other.joda)
    }

    override fun toString(): String {
        return joda.toString()
    }

    override fun adjustInto(temporal: Temporal): Temporal {
        return temporal.with(ChronoField.NANO_OF_DAY, joda.toNanoOfDay().toLong())
    }
}

actual interface ChronoLocalDateTime<D : ChronoLocalDate> : Temporal, Comparable<ChronoLocalDateTime<*>>, TemporalAdjuster {
    actual fun toLocalDate(): D
    actual fun toLocalTime(): LocalTime
    actual fun isAfter(other: ChronoLocalDateTime<*>): Boolean {
        val t = this.joda.unsafeCast<JSJoda.ChronoLocalDateTime>()
        val o = other.joda.unsafeCast<JSJoda.ChronoLocalDateTime>()
        return t.toInstant(ZoneOffset.UTC).isAfter(o.toInstant(ZoneOffset.UTC))
    }

    actual fun isBefore(other: ChronoLocalDateTime<*>): Boolean {
        val t = this.joda.unsafeCast<JSJoda.ChronoLocalDateTime>()
        val o = other.joda.unsafeCast<JSJoda.ChronoLocalDateTime>()
        return t.toInstant(ZoneOffset.UTC).isBefore(o.toInstant(ZoneOffset.UTC))
    }

    actual fun atZone(zone: ZoneId): ChronoZonedDateTime<D>
    actual override fun plus(amountToAdd: Long, unit: TemporalUnit): ChronoLocalDateTime<D>
}

actual interface TemporalAdjuster {
    actual fun adjustInto(temporal: Temporal): Temporal
}

/***********************/


actual class LoggerFactory {
    actual  companion object {
        actual fun getLogger(clz: KClass<*>): Logger {
            return LoggerImpl(clz.simpleName!!)
        }
    }
}
actual interface Logger {
    actual fun info(msg: String, vararg objects: Any)
    actual fun debug(msg: String, vararg objects: Any)
    actual fun warn(msg: String, vararg objects: Any)
    actual fun error(msg: String, vararg objects: Any)
    actual fun trace(msg: String, vararg objects: Any)
}

internal class LoggerImpl(val logname: String): Logger {

    override fun info(msg: String, vararg objects: Any) {
        console.info(msgTs(msg), *objects)
    }

    override fun debug(msg: String, vararg objects: Any) {
        console.log("[debug] " + msgTs(msg), *objects)
    }

    override fun warn(msg: String, vararg objects: Any) {
        console.warn(msgTs(msg), *objects)
    }

    override fun error(msg: String, vararg objects: Any) {
        console.error(msgTs(msg), *objects)
    }

    override fun trace(msg: String, vararg objects: Any) {
        console.log("[trace] " + msgTs(msg), *objects)
    }

    private fun msgTs(msg: String) = "${now().atZone(systemDefault()).toLocalDateTime()}\t [${logname}] - ${msg.replace("{}", "%s")}"
}