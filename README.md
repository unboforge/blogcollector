# blogcollector

This project was created using the [Ktor Project Generator](https://start.ktor.io).

Here are some useful links to get you started:

- [Ktor Documentation](https://ktor.io/docs/home.html)
- [Ktor GitHub page](https://github.com/ktorio/ktor)
- The [Ktor Slack chat](https://app.slack.com/client/T09229ZC6/C0A974TJ9). You'll need to [request an invite](https://surveys.jetbrains.com/s3/kotlin-slack-sign-up) to join.

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

## Building & Running

To build or run the project, use one of the following tasks:

| Task                                    | Description                                                          |
| -----------------------------------------|---------------------------------------------------------------------- |
| `./gradlew test`                        | Run the tests                                                        |
| `./gradlew build`                       | Build everything                                                     |
| `./gradlew buildFatJar`                 | Build an executable JAR of the server with all dependencies included |
| `./gradlew buildImage`                  | Build the docker image to use with the fat JAR                       |
| `./gradlew publishImageToLocalRegistry` | Publish the docker image locally                                     |
| `./gradlew run`                         | Run the server                                                       |
| `./gradlew runDocker`                   | Run using the local docker image                                     |

If the server starts successfully, you'll see the following output:

```
2024-12-04 14:32:45.584 [main] INFO  Application - Application started in 0.303 seconds.
2024-12-04 14:32:45.682 [main] INFO  Application - Responding at http://0.0.0.0:8080
```

## 환경 변수 설정

이 프로젝트는 Supabase PostgreSQL 데이터베이스를 사용합니다. 보안을 위해 데이터베이스 연결 정보는 환경 변수로 설정해야 합니다.

### 환경 변수 설정 방법

#### macOS/Linux:
```bash
export SUPABASE_DB_URL="jdbc:postgresql://aws-1-ap-southeast-2.pooler.supabase.com:5432/postgres"
export SUPABASE_DB_USER="postgres.zqkrzqgahhneczuhxkhc"
export SUPABASE_DB_PASSWORD="your_password_here"
```

#### Windows (PowerShell):
```powershell
$env:SUPABASE_DB_URL="jdbc:postgresql://aws-1-ap-southeast-2.pooler.supabase.com:5432/postgres"
$env:SUPABASE_DB_USER="postgres.zqkrzqgahhneczuhxkhc"
$env:SUPABASE_DB_PASSWORD="your_password_here"
```

#### .env 파일 사용 (권장)

프로젝트 루트에 `.env` 파일을 생성하고 다음 내용을 추가하세요:

```
SUPABASE_DB_URL=jdbc:postgresql://aws-1-ap-southeast-2.pooler.supabase.com:5432/postgres
SUPABASE_DB_USER=postgres.zqkrzqgahhneczuhxkhc
SUPABASE_DB_PASSWORD=your_password_here
```

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
- `SUPABASE_DB_URL=jdbc:postgresql://aws-1-ap-southeast-2.pooler.supabase.com:5432/postgres`
- `SUPABASE_DB_USER=postgres.zqkrzqgahhneczuhxkhc`
- `SUPABASE_DB_PASSWORD=2WR4FnaPA14SQkE8`

애플리케이션이 정상적으로 시작되면 Supabase 연결이 성공한 것입니다.

