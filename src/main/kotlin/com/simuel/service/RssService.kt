package com.simuel.service

import com.apptasticsoftware.rssreader.Item
import com.apptasticsoftware.rssreader.RssReader
import com.simuel.data.database.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import java.time.format.DateTimeFormatter
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * RSS URL 값 객체
 */
data class RssUrl(val value: String) {
    init {
        require(value.isNotBlank()) { "RSS URL은 비어있을 수 없습니다" }
    }
}

/**
 * 회사명 값 객체
 */
data class CompanyName(val value: String) {
    init {
        require(value.isNotBlank()) { "회사명은 비어있을 수 없습니다" }
    }
}

/**
 * 블로그 포스트 URL 값 객체
 */
data class BlogPostUrl(val value: String) {
    init {
        require(value.isNotBlank()) { "블로그 포스트 URL은 비어있을 수 없습니다" }
    }
}

/**
 * 블로그 포스트 제목 값 객체
 */
data class BlogPostTitle(val value: String) {
    companion object {
        fun default(): BlogPostTitle = BlogPostTitle("제목 없음")
    }
}

/**
 * 블로그 포스트 내용 값 객체
 */
data class BlogPostContent(val value: String)

/**
 * RSS URL 리스트 (일급 컬렉션)
 */
class RssUrlList(private val urls: List<RssUrl>) {
    fun isEmpty(): Boolean = urls.isEmpty()

    suspend fun forEach(action: suspend (RssUrl) -> Unit) {
        urls.forEach { action(it) }
    }

    companion object {
        fun of(vararg urls: String): RssUrlList {
            return RssUrlList(urls.map { RssUrl(it) })
        }
    }
}

/**
 * 날짜 파서
 * kotlinx.datetime 사용
 */
class DateParser {
    @OptIn(ExperimentalTime::class)
    fun parsePubDate(pubDateString: String?): Instant {
        if (pubDateString == null) {
            return now()
        }

        return parseRfc1123(pubDateString).getOrElse { parseIso(pubDateString).getOrElse { parseEpoch(pubDateString).getOrElse { now() } } }
    }

    @OptIn(ExperimentalTime::class)
    private fun parseRfc1123(pubDateString: String): Result<Instant> {
        return runCatching {
            // RFC 1123 형식 파싱 (예: "Wed, 21 Oct 2015 07:28:00 GMT")
            // kotlinx.datetime에서는 직접 파싱이 어려우므로 java.time을 통해 변환
            val javaInstant = DateTimeFormatter.RFC_1123_DATE_TIME.parse(pubDateString)
                .let { java.time.ZonedDateTime.from(it).toInstant() }
            javaInstant?.toKotlinxInstant() ?: throw IllegalArgumentException("Invalid RFC 1123 format")
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun parseIso(pubDateString: String): Result<Instant> {
        return runCatching {
            Instant.parse(pubDateString)
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun parseEpoch(pubDateString: String): Result<Instant> {
        return runCatching {
            Instant.fromEpochMilliseconds(pubDateString.toLong())
        }
    }
}

/**
 * 회사명 추출기
 */
class CompanyExtractor {
    fun extractFromUrl(url: RssUrl): CompanyName {
        val urlValue = url.value.lowercase()
        return when {
            urlValue.contains("kakao.com") || urlValue.contains("tech.kakao") -> CompanyName("kakao")
            urlValue.contains("kakaopay.com") || urlValue.contains("tech.kakaopay") -> CompanyName("kakaopay")
            urlValue.contains("kakaobank.com") || urlValue.contains("tech.kakaobank") -> CompanyName("kakaobank")
            urlValue.contains("kakaoenterprise.com") || urlValue.contains("tech.kakaoenterprise") -> CompanyName("kakaoenterprise")
            urlValue.contains("devocean.sk.com") -> CompanyName("sk")
            urlValue.contains("aws.amazon.com") -> CompanyName("aws")
            urlValue.contains("linecorp.com") || urlValue.contains("engineering.line") -> CompanyName("line")
            urlValue.contains("toss.tech") || urlValue.contains("toss.im") -> CompanyName("toss")
            urlValue.contains("woowabros.com") || urlValue.contains("woowahan.com") -> CompanyName("woowabros")
            urlValue.contains("devsisters.com") -> CompanyName("devsisters")
            urlValue.contains("kurly.com") || urlValue.contains("helloworld.kurly.com") -> CompanyName("kurly")
            urlValue.contains("toast.com") || urlValue.contains("meetup.toast.com") -> CompanyName("nhn")
            urlValue.contains("d2.naver.com") -> CompanyName("naver")
            urlValue.contains("feed/myrealtrip") -> CompanyName("myrealtrip")
            urlValue.contains("feed/deliverytechkorea") -> CompanyName("yogiyo")
            urlValue.contains("feed/daangn") -> CompanyName("daangn")
            urlValue.contains("feed/musinsa") -> CompanyName("musinsa")
            urlValue.contains("feed/pinkfong") -> CompanyName("pinkfong")
            urlValue.contains("feed/prnd") -> CompanyName("prnd")
            urlValue.contains("feed/modusign") -> CompanyName("modusign")
            urlValue.contains("feed/naver-dna") -> CompanyName("naver")
            urlValue.contains("feed/29cm") -> CompanyName("29cm")
            urlValue.contains("feed/wantedjobs") -> CompanyName("wanted")
            urlValue.contains("feed/tabling") -> CompanyName("tabling")
            urlValue.contains("feed/naver-place") -> CompanyName("naver")
            urlValue.contains("feed/cj-onstyle") -> CompanyName("cjonstyle")
            urlValue.contains("feed/ctc-mzc") -> CompanyName("megazone")
            urlValue.contains("feed/idus") -> CompanyName("idus")
            urlValue.contains("feed/zigbang") -> CompanyName("zigbang")
            urlValue.contains("feed/class101") -> CompanyName("class101")
            urlValue.contains("feed/yanolja") -> CompanyName("yanolja")
            urlValue.contains("feed/watcha") -> CompanyName("watcha")
            urlValue.contains("feed/loplat") -> CompanyName("loplat")
            urlValue.contains("feed/yanoljacloud") -> CompanyName("yanoljacloud")
            urlValue.contains("feed/aitrics") -> CompanyName("aitrics")
            urlValue.contains("feed/greendatakr") -> CompanyName("greendata")
            urlValue.contains("feed/lunit") -> CompanyName("lunit")
            urlValue.contains("feed/lemonade") -> CompanyName("lemonade")
            urlValue.contains("feed/soomgo") -> CompanyName("soomgo")
            urlValue.contains("feed/dream-youngs") -> CompanyName("dreamyoungs")
            urlValue.contains("feed/vuno") -> CompanyName("vuno")
            urlValue.contains("feed/mathpresso") -> CompanyName("mathpresso")
            urlValue.contains("feed/kmong") -> CompanyName("kmong")
            urlValue.contains("feed/coinone") -> CompanyName("coinone")
            urlValue.contains("feed/platfarm") -> CompanyName("platfarm")
            urlValue.contains("beusable.net") -> CompanyName("beusable")
            urlValue.contains("naver.com") -> CompanyName("naver")
            urlValue.contains("bigwaveai.tistory.com") -> CompanyName("bigwaveai")
            urlValue.contains("cntechsystems.tistory.com") -> CompanyName("cntech")
            urlValue.contains("devlog-h.tistory.com") -> CompanyName("humonlab")
            urlValue.contains("ebay-korea.tistory.com") -> CompanyName("gmarket")
            urlValue.contains("brunch.co.kr/@tmapmobility") -> CompanyName("tmap")
            urlValue.contains("brunch.co.kr/@purpledev") -> CompanyName("kakao")
            urlValue.contains("netmarble.engineering") -> CompanyName("netmarble")
            urlValue.contains("blog.banksalad.com") -> CompanyName("banksalad")
            urlValue.contains("ridicorp.com") -> CompanyName("ridi")
            urlValue.contains("teamdable.github.io") -> CompanyName("teamdable")
            urlValue.contains("dramancompany.com") -> CompanyName("dramancompany")
            urlValue.contains("engineering-skcc.github.io") -> CompanyName("skcnc")
            else -> CompanyName("unknown")
        }
    }
}

/**
 * RSS 아이템 파서
 */
class RssItemParser {
    fun parseLink(item: Item): BlogPostUrl? {
        return item.link.orElse(null)?.takeIf { it.isNotBlank() }?.let { BlogPostUrl(it) }
    }

    fun parseTitle(item: Item): BlogPostTitle {
        return item.title.orElse(null)?.takeIf { it.isNotBlank() }?.let { BlogPostTitle(it) } ?: BlogPostTitle.default()
    }

    fun parseAuthor(item: Item): String? {
        return item.author.orElse(null)?.takeIf { it.isNotBlank() }
    }

    fun parseDescription(item: Item): String? {
        return item.description.orElse(null)?.takeIf { it.isNotBlank() }
    }

    fun parseContent(item: Item): BlogPostContent {
        return BlogPostContent(item.description.orElse(""))
    }

    fun parseThumbnailUrl(item: Item): String? {
        return item.enclosure.orElse(null)?.type?.takeIf { it.contains("image", ignoreCase = true) }
            ?.let { item.enclosure.orElse(null)?.url }
    }
}

/**
 * 블로그 포스트 저장 검증기
 */
class BlogPostValidator {
    suspend fun isDuplicate(url: BlogPostUrl): Boolean {
        return DatabaseFactory.dbQuery {
            BlogPosts.selectAll().where { BlogPosts.url eq url.value }.limit(1).count() > 0
        }
    }
}

/**
 * RSS URL 유효성 검사기
 */
class RssUrlValidator(private val httpClient: HttpClient) {
    suspend fun isValid(url: RssUrl): Boolean {
        return checkWithHead(url) || checkWithGet(url)
    }

    private suspend fun checkWithHead(url: RssUrl): Boolean {
        return runCatching {
            val response = httpClient.head(url.value)
            response.status.isSuccess()
        }.getOrElse { false }
    }

    private suspend fun checkWithGet(url: RssUrl): Boolean {
        return runCatching {
            val response = httpClient.get(url.value)
            response.status.isSuccess()
        }.getOrElse { false }
    }
}

/**
 * 블로그 포스트 저장기
 */
class BlogPostRepository {
    @OptIn(ExperimentalTime::class)
    suspend fun save(
        url: BlogPostUrl,
        title: BlogPostTitle,
        content: BlogPostContent,
        company: CompanyName,
        publishedAt: Instant,
        author: String? = null,
        description: String? = null,
        thumbnailUrl: String? = null,
        sourceRssUrl: String
    ) {
        DatabaseFactory.dbQuery {
            BlogPosts.insert {
                it[BlogPosts.title] = title.value
                it[BlogPosts.content] = content.value
                it[BlogPosts.url] = url.value
                it[BlogPosts.author] = author
                it[BlogPosts.description] = description
                it[BlogPosts.thumbnailUrl] = thumbnailUrl
                it[BlogPosts.sourceBlogName] = company.value
                it[BlogPosts.sourceRssUrl] = sourceRssUrl
                it[BlogPosts.publishedDate] = publishedAt.toJavaInstant()
                it[BlogPosts.fetchedDate] = now().toJavaInstant()
                it[BlogPosts.createdAt] = now().toJavaInstant()
                it[BlogPosts.updatedAt] = now().toJavaInstant()
            }
        }
    }
}

/**
 * RSS 피드 수집기
 */
class RssFeedCollector(
    private val rssReader: RssReader,
    private val itemParser: RssItemParser,
    private val dateParser: DateParser,
    private val companyExtractor: CompanyExtractor,
    private val validator: BlogPostValidator,
    private val repository: BlogPostRepository
) {
    suspend fun collectFeed(url: RssUrl) {
        runCatching {
            rssReader.read(url.value).toList()
        }.onSuccess { items ->
            processItems(items, url)
        }.onFailure { exception ->
            logError("RSS 수집 실패", url.value, exception)
        }
    }

    private suspend fun processItems(items: List<Item>, url: RssUrl) {
        items.forEach { item ->
            processItem(item, url)
        }
    }

    private suspend fun processItem(item: Item, url: RssUrl) {
        runCatching {
            val blogPostUrl = itemParser.parseLink(item) ?: return

            if (validator.isDuplicate(blogPostUrl)) {
                val title = itemParser.parseTitle(item)
                println("⏭️  중복 데이터 스킵: ${title.value}")
                return
            }

            saveBlogPost(item, url, blogPostUrl)
        }.onFailure { exception ->
            logError("블로그 포스트 처리 실패", "", exception)
        }
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun saveBlogPost(item: Item, url: RssUrl, blogPostUrl: BlogPostUrl) {
        val company = companyExtractor.extractFromUrl(url)
        val title = itemParser.parseTitle(item)
        val content = itemParser.parseContent(item)
        val publishedAt = dateParser.parsePubDate(item.pubDate.orElse(null))
        val author = itemParser.parseAuthor(item)
        val description = itemParser.parseDescription(item)
        val thumbnailUrl = itemParser.parseThumbnailUrl(item)

        runCatching {
            repository.save(
                url = blogPostUrl,
                title = title,
                content = content,
                company = company,
                publishedAt = publishedAt,
                author = author,
                description = description,
                thumbnailUrl = thumbnailUrl,
                sourceRssUrl = url.value
            )
            logSuccess("블로그 포스트 저장 완료", title.value)
        }.onFailure { exception ->
            logError("블로그 포스트 저장 실패", title.value, exception)
        }
    }

    private fun logSuccess(message: String, detail: String) {
        println("$message: $detail")
    }

    private fun logError(message: String, detail: String, exception: Throwable) {
        println("$message: $detail - ${exception.message}")
        exception.printStackTrace()
    }
}

/**
 * RSS 수집 서비스
 * 1시간 간격으로 한국 기술 블로그 RSS를 수집하여 데이터베이스에 저장
 */
class RssService {

    private val rssReader = RssReader()
    private val httpClient = HttpClient(CIO) {
        engine {
            requestTimeout = 10000
        }
    }
    private val job: Job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    private val urlValidator = RssUrlValidator(httpClient)
    private val rssFeedCollector = RssFeedCollector(
        rssReader = rssReader,
        itemParser = RssItemParser(),
        dateParser = DateParser(),
        companyExtractor = CompanyExtractor(),
        validator = BlogPostValidator(),
        repository = BlogPostRepository()
    )

    /**
     * RSS 수집 시작
     * 1시간 간격으로 자동 수집 스케줄링
     */
    fun startRssCollection() {
        launchInitialCollection()
        launchScheduledCollection()
    }

    private fun launchInitialCollection() {
        scope.launch {
            collectRssFeeds()
        }
    }

    private fun launchScheduledCollection() {
        scope.launch {
            while (isActive) {
                delay(60 * 60 * 1000) // 1시간
                collectRssFeeds()
            }
        }
    }

    /**
     * RSS 피드 수집 및 저장
     * 한국 기술 블로그 RSS URL 리스트
     */
    private suspend fun collectRssFeeds() {
        val rssUrlList = createRssUrlList()

        if (rssUrlList.isEmpty()) {
            logEmptyUrlList()
            return
        }

        rssUrlList.forEach { url ->
            runCatching {
                if (urlValidator.isValid(url)) {
                    rssFeedCollector.collectFeed(url)
                } else {
                    logInvalidUrl(url.value)
                }
            }.onFailure { exception ->
                logError("RSS 수집 실패", url.value, exception)
            }
        }
    }

    private fun createRssUrlList(): RssUrlList {
        return RssUrlList.of(
            // 자체 플랫폼 블로그
            "https://tech.kakao.com/feed/",
            "https://aws.amazon.com/ko/blogs/tech/feed/",
            "https://engineering.linecorp.com/ko/feed/index.html",
            "https://toss.tech/rss.xml",
            "https://d2.naver.com/d2.atom",
            "https://tech.kakaopay.com/rss.xml",
            "https://helloworld.kurly.com/feed.xml",
            "https://meetup.toast.com/rss",

            // GitHub Pages 기반
            "https://techblog.woowahan.com/feed/",
            "https://tech.devsisters.com/rss.xml",

            // Medium 기반
            "https://medium.com/feed/myrealtrip-product",
            "https://medium.com/feed/deliverytechkorea",
            "https://medium.com/feed/daangn",
            "https://medium.com/feed/musinsa-tech",
            "https://medium.com/feed/pinkfong",
            "https://medium.com/feed/prnd",
            "https://medium.com/feed/modusign",
            "https://medium.com/feed/naver-dna-tech-blog",
            "https://medium.com/feed/29cm",
            "https://medium.com/feed/wantedjobs",
            "https://medium.com/feed/tabling-tech",
            "https://medium.com/feed/naver-place-dev",
            "https://medium.com/feed/cj-onstyle",
            "https://medium.com/feed/ctc-mzc",
            "https://medium.com/feed/idus-tech",
            "https://medium.com/feed/zigbang",
            "https://medium.com/feed/class101",
            "https://medium.com/feed/yanolja",
            "https://medium.com/feed/watcha",
            "https://medium.com/feed/loplat",
            "https://medium.com/feed/yanoljacloud-tech",
            "https://medium.com/feed/aitrics",
            "https://medium.com/feed/greendatakr",
            "https://medium.com/feed/lunit",
            "https://medium.com/feed/lemonade-engineering",
            "https://medium.com/feed/soomgo-tech",
            "https://medium.com/feed/dream-youngs",
            "https://medium.com/feed/vuno-sw-dev",
            "https://medium.com/feed/mathpresso",
            "https://medium.com/feed/kmong",
            "https://medium.com/feed/coinone",
            "https://medium.com/feed/platfarm",

            // Tistory 기반
            "https://www.beusable.net/blog/rss",
            "https://bigwaveai.tistory.com/rss",
            "https://cntechsystems.tistory.com/rss",
            "https://devlog-h.tistory.com/rss",
            "https://ebay-korea.tistory.com/rss",

            // Brunch 기반
            "https://brunch.co.kr/@tmapmobility/rss",
            "https://brunch.co.kr/@purpledev/rss",

            // 기타 플랫폼
            "https://netmarble.engineering/feed/",
            "https://tech.kakaoenterprise.com/feed",
            "https://blog.banksalad.com/rss.xml",
            "https://www.ridicorp.com/feed",
            "https://teamdable.github.io/techblog/feed.xml",
            "https://blog.dramancompany.com/feed/",
            "https://engineering-skcc.github.io/feed.xml"
        )
    }

    private fun logEmptyUrlList() {
        println("RSS URL 리스트가 비어있습니다. RssService.kt의 createRssUrlList() 메서드에서 URL을 추가하세요.")
    }

    private fun logError(message: String, url: String, exception: Throwable) {
        println("$message: $url - ${exception.message}")
        exception.printStackTrace()
    }

    private fun logInvalidUrl(url: String) {
        println("유효하지 않은 RSS URL (연결 불가): $url")
    }

    /**
     * 서비스 종료
     */
    fun stop() {
        httpClient.close()
        job.cancel()
        scope.cancel()
    }
}

