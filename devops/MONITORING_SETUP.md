# 모니터링 스택 통합 가이드

## 개요

로컬 개발 환경에서 Sentry(에러 트래킹)와 Loki(로그 관리)를 Docker Compose로 실행하는 통합 가이드입니다.

## 아키텍처

```
┌─────────────────┐
│  Spring Boot    │
│  Application    │
└────────┬────────┘
         │
    ┌────┴────┐
    │         │
    ▼         ▼
┌────────┐ ┌──────┐
│ Sentry │ │ Loki │
└────────┘ └───┬──┘
              │
              ▼
          ┌─────────┐
          │ Grafana │
          └─────────┘
```

## 빠른 시작

### 1. 환경 변수 설정
```bash
cp .env.example .env
```

`.env` 파일 편집:
```env
# AI API Keys
GOOGLE_API_KEY=your-google-api-key
OPENAI_API_KEY=your-openai-api-key
ANTHROPIC_API_KEY=your-anthropic-api-key

# Sentry
SENTRY_DSN=http://public@localhost:9000/1
SENTRY_ENABLED=true

# Loki
LOKI_ENABLED=true
LOKI_URL=http://localhost:3100/loki/api/v1/push

# Spring Profile
SPRING_PROFILES_ACTIVE=dev
```

### 2. 모니터링 스택 시작
```bash
docker-compose up -d
```

### 3. Sentry 초기 설정 (최초 1회)
```bash
docker exec -it sentry sentry upgrade
```

### 4. 서비스 접속
- **Sentry**: http://localhost:9000
- **Grafana**: http://localhost:3000 (admin/admin)
- **Loki API**: http://localhost:3100

### 5. 애플리케이션 실행
```bash
cd backend
./gradlew bootRun --args='--spring.profiles.active=dev'
```

## 서비스별 상세 가이드

### Sentry (에러 트래킹)
자세한 내용: [SENTRY_SETUP.md](./SENTRY_SETUP.md)

**주요 기능:**
- 예외 및 에러 자동 수집
- 스택 트레이스 분석
- 에러 발생 빈도 추적
- 알림 설정

**테스트:**
```bash
curl -X POST http://localhost:8080/api/v1/ai/generate \
  -H "Content-Type: application/json" \
  -d '{"model": "invalid-model", "prompt": "테스트"}'
```

### Loki (로그 관리)
자세한 내용: [LOKI_SETUP.md](./LOKI_SETUP.md)

**주요 기능:**
- 구조화된 로그 수집
- LogQL을 통한 로그 쿼리
- Grafana 대시보드 연동
- 로그 레벨별 필터링

**Grafana에서 로그 조회:**
```logql
{app="demo", level="ERROR"}
```

## Docker Compose 서비스 구성

| 서비스 | 포트 | 용도 |
|--------|------|------|
| sentry | 9000 | 에러 트래킹 웹 UI |
| loki | 3100 | 로그 수집 API |
| grafana | 3000 | 로그 시각화 대시보드 |
| postgres | 5432 | Sentry 데이터베이스 |
| redis | 6379 | Sentry 캐시 |

## 유용한 명령어

### 전체 스택 관리
```bash
# 시작
docker-compose up -d

# 상태 확인
docker-compose ps

# 로그 확인
docker-compose logs -f

# 중지
docker-compose down

# 데이터 포함 전체 삭제
docker-compose down -v
```

### 개별 서비스 관리
```bash
# Sentry만 재시작
docker-compose restart sentry

# Loki 로그 확인
docker-compose logs -f loki

# Grafana 재시작
docker-compose restart grafana
```

## 모니터링 워크플로우

### 1. 개발 중 에러 발생
1. 애플리케이션에서 예외 발생
2. Sentry에 자동 전송
3. Sentry 웹 UI에서 에러 확인
4. 스택 트레이스 분석

### 2. 로그 분석
1. 애플리케이션 로그가 Loki로 전송
2. Grafana에서 로그 쿼리
3. 시간대별, 레벨별 필터링
4. 대시보드로 시각화

### 3. 문제 해결
1. Sentry에서 에러 패턴 파악
2. Grafana에서 관련 로그 검색
3. 시간대별 상관관계 분석
4. 근본 원인 파악

## 프로덕션 환경 권장사항

### Sentry
- Sentry Cloud 사용 (https://sentry.io)
- 샘플링 레이트 조정 (0.1 = 10%)
- 민감 정보 필터링 설정

### Loki
- Grafana Cloud 사용 또는
- AWS ECS에 Self-hosted 배포
- S3를 스토리지 백엔드로 사용
- 로그 보존 기간 설정 (30일 권장)

### 환경 변수 (프로덕션)
```env
SENTRY_DSN=https://your-key@o123456.ingest.sentry.io/7890123
SENTRY_ENVIRONMENT=production
SENTRY_ENABLED=true
SENTRY_TRACES_SAMPLE_RATE=0.1

LOKI_ENABLED=true
LOKI_URL=https://your-loki-endpoint/loki/api/v1/push
```

## 트러블슈팅

### Sentry 연결 실패
```bash
# Sentry 컨테이너 로그 확인
docker-compose logs sentry

# 데이터베이스 초기화 재실행
docker-compose down -v
docker-compose up -d
docker exec -it sentry sentry upgrade
```

### Loki 로그 전송 실패
```bash
# Loki 상태 확인
curl http://localhost:3100/ready

# 애플리케이션 로그에서 Loki 관련 에러 확인
docker-compose logs -f loki
```

### Grafana 데이터소스 연결 실패
1. Grafana UI > Configuration > Data Sources
2. Loki 데이터소스 선택
3. URL 확인: `http://loki:3100`
4. "Save & Test" 클릭

## 리소스 사용량

로컬 개발 환경 기준:
- **메모리**: 약 2-3GB
- **디스크**: 약 1-2GB (로그 데이터 포함)
- **CPU**: 최소 2코어 권장

## 참고 자료

- [Sentry 공식 문서](https://docs.sentry.io/)
- [Loki 공식 문서](https://grafana.com/docs/loki/latest/)
- [Grafana 공식 문서](https://grafana.com/docs/grafana/latest/)
- [LogQL 쿼리 언어](https://grafana.com/docs/loki/latest/logql/)
