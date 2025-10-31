package com.simuel.plugins

import com.simuel.data.database.DatabaseFactory
import io.ktor.server.application.*

/**
 * Database 플러그인 설정
 * 데이터베이스 연결 및 초기화
 */
fun Application.configureDatabase() {
    // 환경 변수가 설정되지 않은 경우 테스트 모드로 실행
    try {
        DatabaseFactory.init()
    } catch (e: IllegalArgumentException) {
        // 환경 변수가 없으면 테스트용 H2 데이터베이스 사용
        DatabaseFactory.initTest()
    }
}

