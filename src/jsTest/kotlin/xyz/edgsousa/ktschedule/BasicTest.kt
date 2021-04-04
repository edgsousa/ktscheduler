package xyz.edgsousa.ktschedule

import JSJoda.Instant
import JSJoda.ZoneId
import kotlin.test.Test

class BasicTest {
    @Test fun test() {
        val logger = LoggerFactory.getLogger(BasicTest::class)
        logger.info("message is bananas")
        logger.error("Error is error", NumberFormatException("bananas"))
        println("Sysdefault is ${ZoneId.UTC.id()}")
        println("Now is ${Instant.now()}")

        println()
    }
}