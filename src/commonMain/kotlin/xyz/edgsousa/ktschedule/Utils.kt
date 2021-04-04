package xyz.edgsousa.ktschedule

fun nextMidnight(instant: Instant? = null): Instant {
    val now = (instant ?: now()).atZone(systemDefault()).toLocalDateTime()
    return fromZoned(
        ofDateTime(
            now.plus(1, ChronoUnit.DAYS).toLocalDate(),
            LocalTimeOf(0, 0)
        ).atZone(systemDefault())
    )
}

fun todaysMidnight(instant: Instant?): Instant {
    val now = (instant ?: now()).atZone(systemDefault()).toLocalDateTime()
    return fromZoned(
        ofDateTime(
            now.toLocalDate(),
            LocalTimeOf(0, 0)
        ).atZone(systemDefault())
    )
}

fun beforeMidnight(instant: Instant?): Instant {
    return todaysMidnight(instant).plus(-1, ChronoUnit.NANOS) as Instant
}

fun almostMidnight(instant: Instant?): Instant {
    return nextMidnight(instant).plus(-1, ChronoUnit.NANOS) as Instant
}
