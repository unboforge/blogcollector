package com.simuel.data.database

import kotlin.time.Instant
import java.time.Instant as JavaInstant

/**
 * kotlinx.datetime과 java.time 간 변환 헬퍼
 * Exposed는 java.time.Instant를 사용하므로 변환이 필요합니다
 */

/**
 * kotlinx.datetime.Instant를 java.time.Instant로 변환
 */
@OptIn(kotlin.time.ExperimentalTime::class)
fun Instant.toJavaInstant(): JavaInstant {
    return JavaInstant.ofEpochMilli(toEpochMilliseconds())
}

/**
 * java.time.Instant를 kotlinx.datetime.Instant로 변환
 */
@OptIn(kotlin.time.ExperimentalTime::class)
fun JavaInstant.toKotlinxInstant(): Instant {
    return Instant.fromEpochMilliseconds(toEpochMilli())
}

/**
 * 현재 시각 (kotlinx.datetime)
 */
@OptIn(kotlin.time.ExperimentalTime::class)
fun now(): Instant = Instant.fromEpochMilliseconds(System.currentTimeMillis())

