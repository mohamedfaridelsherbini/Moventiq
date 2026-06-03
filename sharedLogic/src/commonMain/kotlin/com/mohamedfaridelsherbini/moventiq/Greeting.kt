package com.mohamedfaridelsherbini.moventiq

class Greeting {
    private val platform = getPlatform()

    fun greet(): String = sayHello(platform.name)
}
