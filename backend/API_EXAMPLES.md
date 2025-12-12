# API 사용 예제

## 1. 텍스트 생성 (일반)

### Google Gemini
```bash
curl -X POST http://localhost:8080/api/v1/ai/generate \
  -H "Content-Type: application/json" \
  -d '{
    "model": "gemini-2.5-flash-lite",
    "prompt": "Spring Boot에 대해 간단히 설명해주세요",
    "maxTokens": 500,
    "temperature": 0.7
  }'
```

### OpenAI GPT
```bash
curl -X POST http://localhost:8080/api/v1/ai/generate \
  -H "Content-Type: application/json" \
  -d '{
    "model": "gpt-3.5-turbo",
    "prompt": "Java 17의 새로운 기능을 알려주세요",
    "maxTokens": 500,
    "temperature": 0.7
  }'
```

### Claude
```bash
curl -X POST http://localhost:8080/api/v1/ai/generate \
  -H "Content-Type: application/json" \
  -d '{
    "model": "claude-3-haiku-20240307",
    "prompt": "Docker Compose 사용법을 알려주세요",
    "maxTokens": 500,
    "temperature": 0.7
  }'
```

## 2. 스트리밍 텍스트 생성

### Google Gemini (지원)
```bash
curl -X POST http://localhost:8080/api/v1/ai/generate/stream \
  -H "Content-Type: application/json" \
  -d '{
    "model": "gemini-2.5-flash",
    "prompt": "Kubernetes에 대해 자세히 설명해주세요",
    "maxTokens": 1000,
    "temperature": 0.7
  }'
```

### JavaScript (Fetch API)
```javascript
const response = await fetch('http://localhost:8080/api/v1/ai/generate/stream', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    model: 'gemini-2.5-flash-lite',
    prompt: '안녕하세요',
    maxTokens: 1000,
    temperature: 0.7
  })
});

const reader = response.body.getReader();
const decoder = new TextDecoder();

while (true) {
  const { done, value } = await reader.read();
  if (done) break;
  
  const chunk = decoder.decode(value);
  console.log(chunk);
}
```

## 3. 모델 목록 조회

### 전체 모델
```bash
curl http://localhost:8080/api/v1/ai/models
```

### 프로바이더별 모델
```bash
# Google 모델
curl http://localhost:8080/api/v1/ai/models/Google

# OpenAI 모델
curl http://localhost:8080/api/v1/ai/models/OpenAI

# Anthropic 모델
curl http://localhost:8080/api/v1/ai/models/Anthropic
```

## 4. 헬스체크

```bash
curl http://localhost:8080/api/v1/ai/health
```

응답 예시:
```json
{
  "Google": true,
  "OpenAI": true,
  "Anthropic": true
}
```

## 5. 에러 처리

### 지원하지 않는 모델
```bash
curl -X POST http://localhost:8080/api/v1/ai/generate \
  -H "Content-Type: application/json" \
  -d '{
    "model": "invalid-model",
    "prompt": "테스트"
  }'
```

응답:
```json
{
  "success": false,
  "message": "모델 invalid-model은(는) Unknown 프로바이더에서 지원하지 않습니다",
  "timestamp": "2024-12-12T10:30:45.123"
}
```

### 유효성 검증 실패
```bash
curl -X POST http://localhost:8080/api/v1/ai/generate \
  -H "Content-Type: application/json" \
  -d '{
    "model": "gemini-2.5-flash-lite",
    "prompt": "",
    "maxTokens": 5000
  }'
```

응답:
```json
{
  "success": false,
  "message": "유효성 검증 실패",
  "data": {
    "prompt": "프롬프트는 필수입니다",
    "maxTokens": "최대 토큰은 4096 이하여야 합니다"
  },
  "timestamp": "2024-12-12T10:30:45.123"
}
```

## 6. 응답 형식

### 성공 응답
```json
{
  "generatedText": "생성된 텍스트 내용...",
  "model": "gemini-2.5-flash-lite",
  "tokensUsed": 150,
  "processingTimeMs": 1234,
  "costUsd": 0.000015
}
```

### 스트리밍 응답
```
Content-Type: text/event-stream

안녕하
세요
! 
무엇을 
도와드릴까요
?
```

## 7. 파라미터 설명

| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|---------|------|------|--------|------|
| prompt | String | O | - | 생성할 텍스트의 프롬프트 |
| model | String | X | 프로바이더별 기본 모델 | 사용할 AI 모델 ID |
| maxTokens | Integer | X | 1000 | 최대 생성 토큰 수 (1-4096) |
| temperature | Double | X | 0.7 | 생성 온도 (0.0-2.0) |

## 8. 비용 계산

각 요청의 응답에 `costUsd` 필드가 포함되어 있습니다:

```json
{
  "costUsd": 0.000015
}
```

이는 입력 토큰과 출력 토큰을 기반으로 계산된 USD 단위의 비용입니다.
