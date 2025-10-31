package com.simuel.data.database

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

/**
 * 블로그 포스트 테이블 정의
 * Supabase 데이터베이스 스키마와 일치
 */
object BlogPosts : Table("blog_posts") {
    val id = long("id").autoIncrement()

    override val primaryKey = PrimaryKey(id)
    val title = text("title")
    val url = text("url").uniqueIndex()
    val author = text("author").nullable()
    val description = text("description").nullable()
    val content = text("content")
    val thumbnailUrl = text("thumbnail_url").nullable()
    val sourceBlogName = text("source_blog_name")
    val sourceRssUrl = text("source_rss_url")
    val publishedDate = timestamp("published_date")

    @OptIn(kotlin.time.ExperimentalTime::class)
    val fetchedDate = timestamp("fetched_date").clientDefault { now().toJavaInstant() }

    @OptIn(kotlin.time.ExperimentalTime::class)
    val createdAt = timestamp("created_at").clientDefault { now().toJavaInstant() }

    @OptIn(kotlin.time.ExperimentalTime::class)
    val updatedAt = timestamp("updated_at").clientDefault { now().toJavaInstant() }
}

/**
 * 테이블 관리 클래스
 */
object Tables {
    /**
     * 모든 테이블 생성
     */
    fun createTables() {
        SchemaUtils.create(BlogPosts)
    }
}

