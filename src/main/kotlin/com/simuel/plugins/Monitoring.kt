package com.simuel.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.calllogging.*

/**
 * Monitoring 플러그인 설정
 * 요청 로깅 설정
 */
fun Application.configureMonitoring() {
    install(CallLogging) {
        level = org.slf4j.event.Level.INFO
    }
}

