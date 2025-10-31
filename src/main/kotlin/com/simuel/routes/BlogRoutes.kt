package com.simuel.routes

import com.simuel.data.database.BlogPosts
import com.simuel.data.database.DatabaseFactory
import com.simuel.data.database.toKotlinxInstant
import com.simuel.data.models.BlogPost
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import kotlin.time.ExperimentalTime

/**
 * 블로그 포스트 라우트 정의
 * REST API 엔드포인트 제공
 */
fun Route.blogRoutes() {
    route("/api/v1/blogs") {
        /**
         * 전체 블로그 포스트 조회
         * Query Parameters:
         *   - company: 회사명 필터링 (선택)
         *   - search: 검색어 (제목 또는 내용에서 검색) (선택)
         *   - page: 페이지 번호 (기본값: 1) (선택)
         *   - size: 페이지 크기 (기본값: 20) (선택)
         */
        get {
            val company = call.request.queryParameters["company"]
            val search = call.request.queryParameters["search"]
            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
            val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 20
            val offset = (page - 1) * size

            val posts = DatabaseFactory.dbQuery {
                var query: Query = BlogPosts.selectAll()

                // 회사 필터링 (source_blog_name으로 검색)
                if (!company.isNullOrBlank()) {
                    query = query.where { BlogPosts.sourceBlogName eq company }
                }

                // 검색 필터링
                if (!search.isNullOrBlank()) {
                    query = query.where {
                        BlogPosts.title.like("%$search%") or BlogPosts.content.like("%$search%")
                    }
                }

                // 정렬 및 페이징
                query.orderBy(BlogPosts.publishedDate to SortOrder.DESC).limit(size).offset(offset.toLong())
                    .map { rowToBlogPost(it) }
            }

            call.respond(HttpStatusCode.OK, posts)
        }
    }
}

/**
 * 데이터베이스 행을 BlogPost 모델로 변환
 * kotlinx.datetime 사용
 */
@OptIn(ExperimentalTime::class)
private fun rowToBlogPost(row: ResultRow): BlogPost {
    return BlogPost(
        id = row[BlogPosts.id],
        title = row[BlogPosts.title],
        url = row[BlogPosts.url],
        author = row[BlogPosts.author],
        description = row[BlogPosts.description],
        content = row[BlogPosts.content],
        thumbnailUrl = row[BlogPosts.thumbnailUrl],
        sourceBlogName = row[BlogPosts.sourceBlogName],
        sourceRssUrl = row[BlogPosts.sourceRssUrl],
        publishedDate = row[BlogPosts.publishedDate].toKotlinxInstant().toString(),
        fetchedDate = row[BlogPosts.fetchedDate].toKotlinxInstant().toString(),
        createdAt = row[BlogPosts.createdAt].toKotlinxInstant().toString(),
        updatedAt = row[BlogPosts.updatedAt].toKotlinxInstant().toString()
    )
}

