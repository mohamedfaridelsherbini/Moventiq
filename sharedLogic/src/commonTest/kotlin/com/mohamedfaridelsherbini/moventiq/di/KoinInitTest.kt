package com.mohamedfaridelsherbini.moventiq.di

import com.mohamedfaridelsherbini.moventiq.Greeting
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertTrue

class KoinInitTest : KoinTest {
    private val greeting: Greeting by inject()

    @AfterTest
    fun tearDown() {
        resetKoinForTests()
    }

    @Test
    fun initKoin_registers_shared_dependencies() {
        initKoin()

        assertTrue(isKoinInitialized())
        assertTrue(greeting.greet().contains("Hello"))
    }

    @Test
    fun initKoin_is_idempotent() {
        initKoin()
        initKoin()

        assertTrue(isKoinInitialized())
    }
}
