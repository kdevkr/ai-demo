# Loki 로컬 설정 가이드

## 개요

Loki는 Grafana에서 개발한 로그 수집 및 쿼리 시스템입니다. 로컬 환경에서 Docker Compose를 통해 Loki와 Grafana를 실행하여 애플리케이션 로그를 모니터링할 수 있습니다.

## 1. Docker Compose로 Loki 및 Grafana 실행

### 전체 스택 시작
```bash
docker-compose up -d
```

이 명령으로 다음 서비스들이 시작됩니다:
- Loki (포트 3100)
- Grafana (포트 3000)
- Sentry (포트 9000)
- Redis, PostgreSQL

### 개별 서비스 시작
```bash
# Loki와 Grafana만 시작
docker-compose up -d loki grafana
```

## 2. Grafana 접속 및 설정

### 초기 접속
- URL: http://localhost:3000
- 기본 계정:
  - Username: `admin`
  - Password: `admin`
- 첫 로그인 시 비밀번호 변경 권장

### Loki 데이터소스 확인
1. 좌측 메뉴 > Configuration > Data Sources
2. Loki 데이터소스가 자동으로 구성되어 있음
3. "Test" 버튼으로 연결 확인

## 3. 애플리케이션 로그 전송

### 환경 변수 설정
`.env` 파일에 Loki 설정 추가:
```env
LOKI_ENABLED=true
LOKI_URL=http://localhost:3100/loki/api/v1/push
```

### 애플리케이션 실행
```bash
cd backend
./gradlew bootRun --args='--spring.profiles.active=dev'
```

애플리케이션이 실행되면 자동으로 Loki에 로그가 전송됩니다.

## 4. Grafana에서 로그 조회

### Explore 사용
1. 좌측 메뉴 > Explore
2. 데이터소스: Loki 선택
3. Log browser에서 레이블 선택:
   - `app="demo"`: 애플리케이션 이름
   - `level="INFO"`, `level="ERROR"` 등: 로그 레벨
   - `host`: 호스트명

### 쿼리 예제

**모든 로그 조회**
```logql
{app="demo"}
```

**에러 로그만 조회**
```logql
{app="demo", level="ERROR"}
```

**특정 텍스트 포함 로그**
```logql
{app="demo"} |= "AI 서비스"
```

**특정 로거의 로그**
```logql
{app="demo"} | json | logger =~ "com.example.demo.*"
```

**시간대별 에러 카운트**
```logql
sum(count_over_time({app="demo", level="ERROR"}[5m]))
```

## 5. 대시보드 생성

### 기본 대시보드 생성
1. 좌측 메�우 > Dashboards > New Dashboard
2. Add visualization 클릭
3. 데이터소스: Loki 선택
4. 쿼리 입력 및 시각화 타입 선택

### 추천 패널

**로그 레벨별 분포**
```logql
sum by (level) (count_over_time({app="demo"}[5m]))
```

**최근 에러 로그**
```logql
{app="demo", level="ERROR"}
```

**API 응답 시간 (로그에서 추출)**
```logql
{app="demo"} | json | message =~ ".*처리 완료.*"
```

## 6. 로그 레이블 구조

애플리케이션에서 전송되는 로그의 레이블:
- `app`: 애플리케이션 이름 (demo)
- `host`: 호스트명
- `level`: 로그 레벨 (INFO, DEBUG, WARN, ERROR)

로그 메시지는 JSON 형식:
```json
{
  "timestamp": "2024-12-12T10:30:45.123",
  "level": "INFO",
  "thread": "http-nio-8080-exec-1",
  "logger": "com.example.demo.service.OpenAIService",
  "message": "텍스트 생성 완료",
  "exception": ""
}
```

## 7. Docker Compose 명령어

```bash
# 전체 시작
docker-compose up -d

# 로그 확인
docker-compose logs -f loki
docker-compose logs -f grafana

# 특정 서비스 재시작
docker-compose restart loki

# 중지
docker-compose down

# 데이터 포함 전체 삭제
docker-compose down -v
```

## 8. 프로덕션 환경 설정

### AWS 환경에서 Loki 사용

**옵션 1: Grafana Cloud**
- https://grafana.com/products/cloud/ 에서 계정 생성
- Loki 엔드포인트 및 인증 정보 획득
- 환경 변수 설정:
  ```env
  LOKI_ENABLED=true
  LOKI_URL=https://logs-prod-us-central1.grafana.net/loki/api/v1/push
  ```

**옵션 2: Self-hosted on ECS**
- Loki를 ECS 서비스로 배포
- S3를 스토리지 백엔드로 사용
- Application Load Balancer로 접근 제어

### 로그 보존 정책
프로덕션 환경에서는 `loki-config.yaml`에서 보존 기간 설정:
```yaml
limits_config:
  retention_period: 720h  # 30일
table_manager:
  retention_deletes_enabled: true
  retention_period: 720h
```

## 트러블슈팅

### Loki에 로그가 전송되지 않는 경우
```bash
# Loki 상태 확인
curl http://localhost:3100/ready

# 애플리케이션 로그 확인
# logback-spring.xml의 LOKI appender 설정 확인
```

### Grafana에서 로그가 보이지 않는 경우
1. 데이터소스 연결 테스트
2. 시간 범위 확인 (우측 상단)
3. 레이블 필터 확인

### 디스크 공간 부족
```bash
# Loki 데이터 정리
docker-compose down
docker volume rm ai-demo_loki-data
docker-compose up -d
```

## 참고 자료

- [Loki 공식 문서](https://grafana.com/docs/loki/latest/)
- [LogQL 쿼리 언어](https://grafana.com/docs/loki/latest/logql/)
- [Grafana 대시보드 가이드](https://grafana.com/docs/grafana/latest/dashboards/)
