package com.simuel.data.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.cdimascio.dotenv.dotenv
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * 데이터베이스 팩토리 클래스
 * Supabase PostgreSQL 연결 및 HikariCP 커넥션 풀 관리
 */
object DatabaseFactory {

    private val dotenv = dotenv {
        ignoreIfMissing = true
        systemProperties = false
    }

    /**
     * 데이터베이스 초기화
     * 환경 변수에서 설정을 읽어서 PostgreSQL 연결 생성
     */
    fun init() {
        val databaseUrl = dotenv["SUPABASE_DB_URL"] ?: System.getenv("SUPABASE_DB_URL")
            ?: throw IllegalArgumentException("SUPABASE_DB_URL environment variable is not set")
        val databaseUser = dotenv["SUPABASE_DB_USER"] ?: System.getenv("SUPABASE_DB_USER")
            ?: throw IllegalArgumentException("SUPABASE_DB_USER environment variable is not set")
        val databasePassword = dotenv["SUPABASE_DB_PASSWORD"] ?: System.getenv("SUPABASE_DB_PASSWORD")
            ?: throw IllegalArgumentException("SUPABASE_DB_PASSWORD environment variable is not set")

        // 디버깅: 로드된 설정 출력
        println("=== Database Configuration ===")
        println("URL: $databaseUrl")
        println("User: $databaseUser")
        println("Password: ${databasePassword.take(3)}***")
        println("==============================")
        
        // HikariCP 설정
        val config = HikariConfig().apply {
            jdbcUrl = databaseUrl
            username = databaseUser
            password = databasePassword
            driverClassName = "org.postgresql.Driver"
            maximumPoolSize = 10
            minimumIdle = 2
            connectionTimeout = 30000
            idleTimeout = 600000
            maxLifetime = 1800000
        }
        
        val dataSource = HikariDataSource(config)
        Database.connect(dataSource)
        
        // 테이블 생성
        transaction {
            Tables.createTables()
        }
    }
    
    /**
     * 테스트용 데이터베이스 초기화 (H2 인메모리)
     */
    fun initTest() {
        val databaseUrl = System.getenv("SUPABASE_DB_URL")
        val databaseUser = System.getenv("SUPABASE_DB_USER")
        val databasePassword = System.getenv("SUPABASE_DB_PASSWORD")
        
        if (databaseUrl != null && databaseUser != null && databasePassword != null) {
            init()
        } else {
            // 테스트 환경에서는 H2 인메모리 데이터베이스 사용
            Database.connect(
                url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
                user = "root",
                driver = "org.h2.Driver",
                password = ""
            )
            transaction {
                Tables.createTables()
            }
        }
    }
    
    /**
     * 코루틴 기반 데이터베이스 트랜잭션 헬퍼
     */
    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction { block() }
}

