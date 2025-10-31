package com.simuel

import com.simuel.plugins.*
import com.simuel.service.RssService
import io.ktor.server.application.*

/**
 * 애플리케이션 진입점
 */
fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

/**
 * Ktor 애플리케이션 모듈 설정
 */
fun Application.module() {
    // 플러그인 설정
    configureSerialization()
    configureCors()
    configureDatabase()
    configureRouting()
    configureMonitoring()

    // RSS 수집 서비스 시작
    val rssService = RssService()
    rssService.startRssCollection()

    // 애플리케이션 종료 시 정리
    monitor.subscribe(ApplicationStopped) {
        rssService.stop()
    }
}
