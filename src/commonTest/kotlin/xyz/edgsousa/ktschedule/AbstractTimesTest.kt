package xyz.edgsousa.ktschedule

abstract class AbstractTimesTest {
    val z = systemDefault()
    val d = now().atZone(z).toLocalDateTime()
}