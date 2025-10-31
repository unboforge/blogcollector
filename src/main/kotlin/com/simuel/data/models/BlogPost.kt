package com.simuel.data.models

import kotlinx.serialization.Serializable

/**
 * 블로그 포스트 데이터 모델
 * Supabase 데이터베이스 스키마와 일치
 */
@Serializable
data class BlogPost(
    val id: Long,
    val title: String,
    val url: String,
    val author: String? = null,
    val description: String? = null,
    val content: String,
    val thumbnailUrl: String? = null,
    val sourceBlogName: String,
    val sourceRssUrl: String,
    val publishedDate: String, // ISO 8601 형식의 날짜 문자열
    val fetchedDate: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

