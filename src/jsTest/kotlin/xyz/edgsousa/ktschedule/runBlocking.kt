package xyz.edgsousa.ktschedule

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.promise


val testScope = MainScope()
actual fun runTest(block: suspend CoroutineScope.() -> Unit): dynamic = testScope.promise { block() }