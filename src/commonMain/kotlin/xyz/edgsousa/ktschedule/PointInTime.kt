package xyz.edgsousa.ktschedule

/**
 * Interface for any value or method describing a moment in time
 */
interface PointInTime: Comparable<PointInTime> {

    /**
     * The next absolute time strictly after 'now'
     * @param instant Defaults to 'now' by the system clock
     */
    fun next(instant: Instant? = null): Instant?

    /**
     * Returns the next, or the first the next day
     */
    fun nextRepeating(instant: Instant? = null): Instant? {
        return next(instant) ?: next(nextMidnight(instant))
    }

    /**
     * The most recent time before (or equal) to 'instant'
     */
    fun previous(instant: Instant? = null): Instant?

    /**
     * Returns the previous, or last previous day
     */
    fun previousRepeating(instant: Instant?): Instant? {
        return previous(instant) ?: previous(beforeMidnight(instant))
    }

    /**
     * Default compare will sort by ascending next instant
     * null instants will never happen, go to end of sorting
     */
    override fun compareTo(other: PointInTime): Int {
        val n = this.nextRepeating()
        val o = other.nextRepeating()
        if (n == null && o == null) return 0
        if (n == null) return 1
        if (o == null) return -1
        return n.compareTo(o)
    }

}