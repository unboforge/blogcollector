package com.simuel.plugins

import com.simuel.routes.blogRoutes
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Routing 플러그인 설정
 * API 라우트 등록
 */
fun Application.configureRouting() {
    routing {
        // 헬스 체크 엔드포인트
        get("/") {
            call.respond(mapOf("status" to "OK", "message" to "Korean Tech Blog RSS Collector API"))
        }
        
        // 블로그 포스트 라우트
        blogRoutes()
    }
}

