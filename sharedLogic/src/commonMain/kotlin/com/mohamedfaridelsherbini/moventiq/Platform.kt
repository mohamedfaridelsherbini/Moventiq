package com.mohamedfaridelsherbini.moventiq

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform