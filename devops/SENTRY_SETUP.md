# Sentry 로컬 설정 가이드

## 1. Docker Compose로 Sentry 실행

### Sentry 시작
```bash
docker-compose up -d
```

### 초기 설정 (최초 1회만)
```bash
# Sentry 컨테이너에 접속하여 초기 설정 실행
docker exec -it sentry sentry upgrade

# 관리자 계정 생성 (프롬프트에 따라 이메일, 비밀번호 입력)
```

### Sentry 웹 UI 접속
- URL: http://localhost:9000
- 생성한 관리자 계정으로 로그인

## 2. Sentry 프로젝트 생성

1. 로그인 후 "Create Project" 클릭
2. Platform: **Java Spring Boot** 선택
3. Project Name: `ai-demo-backend` 입력
4. Team: Default 선택
5. "Create Project" 클릭

## 3. DSN 확인 및 설정

1. 프로젝트 생성 후 표시되는 DSN 복사
   - 형식: `http://[public_key]@localhost:9000/[project_id]`
   
2. `.env` 파일 생성 (`.env.example` 참고)
   ```bash
   cp .env.example .env
   ```

3. `.env` 파일에 DSN 설정
   ```env
   SENTRY_DSN=http://your-public-key@localhost:9000/1
   SENTRY_ENABLED=true
   ```

## 4. 애플리케이션 실행

```bash
cd backend
./gradlew bootRun --args='--spring.profiles.active=dev'
```

## 5. 에러 테스트

### 테스트 API 호출
```bash
# 존재하지 않는 모델로 요청하여 에러 발생
curl -X POST http://localhost:8080/api/v1/ai/generate \
  -H "Content-Type: application/json" \
  -d '{
    "model": "invalid-model",
    "prompt": "테스트"
  }'
```

### Sentry에서 확인
1. http://localhost:9000 접속
2. 프로젝트 선택
3. Issues 탭에서 발생한 에러 확인

## 6. Docker Compose 명령어

```bash
# 시작
docker-compose up -d

# 중지
docker-compose down

# 로그 확인
docker-compose logs -f sentry

# 전체 삭제 (데이터 포함)
docker-compose down -v
```

## 7. 프로덕션 환경 설정

프로덕션에서는 Sentry Cloud 사용 권장:
1. https://sentry.io 에서 계정 생성
2. 프로젝트 생성 후 DSN 획득
3. 환경 변수 설정:
   ```env
   SENTRY_DSN=https://your-key@o123456.ingest.sentry.io/7890123
   SENTRY_ENVIRONMENT=production
   SENTRY_ENABLED=true
   SENTRY_TRACES_SAMPLE_RATE=0.1
   ```

## 트러블슈팅

### Sentry 컨테이너가 시작되지 않는 경우
```bash
# 로그 확인
docker-compose logs sentry

# 데이터베이스 초기화 재실행
docker-compose down -v
docker-compose up -d
docker exec -it sentry sentry upgrade
```

### DSN 연결 오류
- Docker 네트워크 확인: `docker network ls`
- Sentry 컨테이너 상태 확인: `docker ps`
- 포트 9000이 사용 중인지 확인
