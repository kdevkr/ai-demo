# AI Demo Backend

Spring Boot 기반 멀티 AI 프로바이더 통합 백엔드 애플리케이션

## 기술 스택

- Java 17
- Spring Boot 3.5.8
- Gradle 8.5
- Sentry (에러 트래킹)
- Loki (로그 관리)

## 지원 AI 프로바이더

- OpenAI (GPT-5.1, GPT-5 Mini, GPT-3.5 Turbo)
- Google Gemini (Gemini 3 Pro, Gemini 2.5 Flash, Gemini 2.5 Flash Lite)
- Anthropic Claude (Claude 4.5 Sonnet, Claude 4.5 Haiku, Claude 3.5 Haiku, Claude 3 Haiku)

## 환경 설정

### 1. 환경 변수 설정

```bash
cp ../.env.example ../.env
```

`.env` 파일에 API 키 설정:
```env
GOOGLE_API_KEY=your-google-api-key
OPENAI_API_KEY=your-openai-api-key
ANTHROPIC_API_KEY=your-anthropic-api-key
SENTRY_DSN=http://public@localhost:9000/1
LOKI_ENABLED=true
```

### 2. 모니터링 스택 실행 (선택사항)

로컬 개발 환경에서 Sentry, Loki, Grafana 사용:
```bash
# 프로젝트 루트에서
cd devops
docker-compose up -d
```

자세한 내용:
- 통합 가이드: `../devops/MONITORING_SETUP.md`
- Sentry 설정: `../devops/SENTRY_SETUP.md`
- Loki 설정: `../devops/LOKI_SETUP.md`

## 빌드 및 실행

### 빌드
```bash
./gradlew build
```

### 개발 환경 실행
```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### 프로덕션 환경 실행
```bash
./gradlew bootRun --args='--spring.profiles.active=prod'
```

## API 엔드포인트

### 텍스트 생성
```bash
POST /api/v1/ai/generate
Content-Type: application/json

{
  "model": "gemini-2.5-flash-lite",
  "prompt": "안녕하세요",
  "maxTokens": 1000,
  "temperature": 0.7
}
```

### 스트리밍 텍스트 생성
```bash
POST /api/v1/ai/generate/stream
Content-Type: application/json

{
  "model": "gemini-2.5-flash-lite",
  "prompt": "안녕하세요",
  "maxTokens": 1000,
  "temperature": 0.7
}
```

**지원 모델:**
- Google Gemini: 모든 모델 지원
- OpenAI: 모든 모델 지원
- Anthropic Claude: 모든 모델 지원

### 모델 목록 조회
```bash
GET /api/v1/ai/models
```

### 프로바이더별 모델 조회
```bash
GET /api/v1/ai/models/{provider}
```

### 헬스체크
```bash
GET /api/v1/ai/health
```

## 테스트

```bash
./gradlew test
```

## 프로파일

- `dev`: 개발 환경 (디버그 로그, CORS 허용)
- `prod`: 프로덕션 환경 (최소 로그, Sentry 활성화)
