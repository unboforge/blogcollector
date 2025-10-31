# Blog Collector

한국 주요 테크 기업들의 기술 블로그 RSS 피드를 자동으로 수집하여 Supabase 데이터베이스에 저장하는 서비스입니다.

## 주요 기능

- 🔄 **자동 RSS 수집**: 1시간 간격으로 자동 수집
- 🚫 **중복 방지**: URL 기반 중복 데이터 자동 필터링
- 📊 **RESTful API**: 수집된 블로그 포스트 조회 API 제공
- 💾 **Supabase 연동**: PostgreSQL 데이터베이스에 안전하게 저장

## 수집 대상 블로그

- 카카오 기술 블로그
- 네이버 기술 블로그
- 라인 기술 블로그
- 우아한형제들 기술 블로그
- 당근마켓 기술 블로그
- AWS 한국 블로그
- 토스 기술 블로그
- 기타 한국 주요 테크 기업 블로그

## 기술 스택

- **Backend**: Ktor (Kotlin)
- **Database**: Supabase PostgreSQL
- **RSS Parser**: RSS Reader Library
- **HTTP Client**: Ktor CIO Client

## Features

Here's a list of features included in this project:

| Name                                                                   | Description                                                                        |
| ------------------------------------------------------------------------|------------------------------------------------------------------------------------ |
| [Routing](https://start.ktor.io/p/routing)                             | Provides a structured routing DSL                                                  |
| [kotlinx.serialization](https://start.ktor.io/p/kotlinx-serialization) | Handles JSON serialization using kotlinx.serialization library                     |
| [Content Negotiation](https://start.ktor.io/p/content-negotiation)     | Provides automatic content conversion according to Content-Type and Accept headers |
| [Exposed](https://start.ktor.io/p/exposed)                             | Adds Exposed database to your application                                          |
| [Call Logging](https://start.ktor.io/p/call-logging)                   | Logs client requests                                                               |
| [CORS](https://start.ktor.io/p/cors)                                   | Enables Cross-Origin Resource Sharing (CORS)                                       |

## 빠른 시작

### 서버 실행

```bash
# 서버 시작
./run.sh

# 서버 중지
./stop.sh
```

### Gradle 명령어

| Task                                    | Description                                                          |
| -----------------------------------------|---------------------------------------------------------------------- |
| `./gradlew test`                        | Run the tests                                                        |
| `./gradlew build`                       | Build everything                                                     |
| `./gradlew run`                         | Run the server                                                       |

If the server starts successfully, you'll see the following output:

```
2025-10-30 23:42:32.075 [main] INFO  Application - Application started in 3.029 seconds.
2025-10-30 23:42:33.246 [DefaultDispatcher-worker-6] INFO  Application - Responding at http://0.0.0.0:8080
✅ 블로그 포스트 저장 완료: ...
```

## API 엔드포인트

### 블로그 포스트 조회

**GET** `/api/v1/blogs`

#### Query Parameters

| Parameter | Type   | Required | Description                    |
|-----------|--------|----------|--------------------------------|
| company   | String | No       | 회사명으로 필터링 (예: "kakao") |
| search    | String | No       | 제목/내용으로 검색              |
| page      | Int    | No       | 페이지 번호 (기본값: 0)         |
| size      | Int    | No       | 페이지 크기 (기본값: 20)        |

#### 응답 예시

```json
[
  {
    "id": 1,
    "title": "카카오 기술 블로그 포스트 제목",
    "url": "https://tech.kakao.com/posts/123",
    "description": "포스트 설명...",
    "content": "포스트 내용...",
    "sourceBlogName": "kakao",
    "sourceRssUrl": "https://tech.kakao.com/feed/",
    "publishedDate": "2025-10-30T00:00:00Z",
    "fetchedDate": "2025-10-30T14:42:33.784Z",
    "createdAt": "2025-10-30T14:42:33.784Z",
    "updatedAt": "2025-10-30T14:42:33.784Z"
  }
]
```

#### 사용 예시

```bash
# 전체 조회
curl "http://localhost:8080/api/v1/blogs"

# 카카오 블로그만 조회
curl "http://localhost:8080/api/v1/blogs?company=kakao"

# 검색
curl "http://localhost:8080/api/v1/blogs?search=AI"

# 페이징
curl "http://localhost:8080/api/v1/blogs?page=0&size=10"
```

## 환경 변수 설정

이 프로젝트는 Supabase PostgreSQL 데이터베이스를 사용합니다. 보안을 위해 데이터베이스 연결 정보는 환경 변수로 설정해야 합니다.

### 환경 변수 설정 방법

#### macOS/Linux:
```bash
export SUPABASE_DB_URL="your_supabase_jdbc_url"
export SUPABASE_DB_USER="your_supabase_user"
export SUPABASE_DB_PASSWORD="your_password_here"
```

#### Windows (PowerShell):
```powershell
$env:SUPABASE_DB_URL="your_supabase_jdbc_url"
$env:SUPABASE_DB_USER="your_supabase_user"
$env:SUPABASE_DB_PASSWORD="your_password_here"
```

#### .env 파일 사용 (권장)

프로젝트 루트에 `.env` 파일을 생성하고 다음 내용을 추가하세요:

```
# Supabase Database Configuration
SUPABASE_DB_URL=your_supabase_jdbc_url
SUPABASE_DB_USER=your_supabase_user
SUPABASE_DB_PASSWORD=your_password_here
```

> ⚠️ **보안 주의**: `.env` 파일은 `.gitignore`에 포함되어 있어 Git에 커밋되지 않습니다.

그리고 실행 시 환경 변수를 로드하세요:

```bash
# macOS/Linux: .env 파일에서 환경 변수 로드
export $(cat .env | xargs)
./gradlew run
```

또는 `source` 명령어를 사용할 수 있습니다:

```bash
source .env
./gradlew run
```

### 테스트 실행

기본적으로 테스트는 H2 인메모리 데이터베이스를 사용합니다. 환경 변수가 설정되지 않은 경우 자동으로 H2를 사용합니다:

```bash
./gradlew test
```

실제 Supabase 연결을 테스트하려면 환경 변수를 설정한 후 테스트를 실행하세요:

```bash
# 환경 변수 설정 후
export $(cat .env | xargs)
./gradlew test
```

또는 IDE에서 실행할 경우, 실행 설정에서 환경 변수를 추가하세요.

### Supabase 연결 확인

**`.env` 파일이 이미 생성되어 있습니다.** 필요시 수정하세요.

#### 실행 방법

**방법 1: 실행 스크립트 사용 (권장)**
```bash
./run.sh
```

**방법 2: 수동 실행**
```bash
export $(cat .env | xargs)
./gradlew run
```

**방법 3: IDE에서 실행**
실행 설정에서 다음 환경 변수를 추가하세요:
- `SUPABASE_DB_URL=your_supabase_jdbc_url`
- `SUPABASE_DB_USER=your_supabase_user`
- `SUPABASE_DB_PASSWORD=your_password_here`

애플리케이션이 정상적으로 시작되면 Supabase 연결이 성공한 것입니다.

## RSS 수집 기능

### 자동 수집
- **수집 주기**: 1시간 간격
- **중복 방지**: URL 기반 자동 중복 체크
- **로그 출력**:
  - ✅ 새로운 포스트 저장 시: `블로그 포스트 저장 완료: [제목]`
  - ⏭️ 중복 포스트 스킵 시: `중복 데이터 스킵: [제목]`

### 중복 방지 메커니즘
1. **데이터베이스 레벨**: URL 컬럼에 unique index 설정
2. **애플리케이션 레벨**: 저장 전 기존 URL 존재 여부 확인
3. **자동 스킵**: 중복된 URL은 자동으로 건너뛰고 새로운 데이터만 저장

### 수집 상태 확인
```bash
# API로 최근 수집된 데이터 확인
curl "http://localhost:8080/api/v1/blogs?page=0&size=5"
```

