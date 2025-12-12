# DevOps 설정

로컬 개발 및 프로덕션 배포를 위한 DevOps 설정 파일 모음

## 디렉토리 구조

```
devops/
├── docker-compose.yml          # 로컬 모니터링 스택
├── loki-config.yaml           # Loki 설정
├── grafana-datasources.yaml   # Grafana 데이터소스
├── MONITORING_SETUP.md        # 통합 모니터링 가이드
├── SENTRY_SETUP.md           # Sentry 설정 가이드
└── LOKI_SETUP.md             # Loki 설정 가이드
```

## 로컬 개발 환경

### 모니터링 스택 시작
```bash
cd devops
docker-compose up -d
```

### 서비스 접속
- **Sentry**: http://localhost:9000
- **Grafana**: http://localhost:3000 (admin/admin)
- **Loki API**: http://localhost:3100

### 상세 가이드
- [통합 모니터링 설정](./MONITORING_SETUP.md)
- [Sentry 설정](./SENTRY_SETUP.md)
- [Loki 설정](./LOKI_SETUP.md)

## 프로덕션 환경

### AWS 인프라
- **Backend**: ECS Fargate
- **Frontend**: CloudFront + S3
- **Monitoring**: Grafana Cloud + Sentry Cloud
- **Logs**: Loki (Self-hosted on ECS 또는 Grafana Cloud)

### 환경 변수 (프로덕션)
```env
SENTRY_DSN=https://your-key@o123456.ingest.sentry.io/7890123
SENTRY_ENVIRONMENT=production
SENTRY_ENABLED=true
SENTRY_TRACES_SAMPLE_RATE=0.1

LOKI_ENABLED=true
LOKI_URL=https://your-loki-endpoint/loki/api/v1/push
```

## 유용한 명령어

```bash
# 전체 시작
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
