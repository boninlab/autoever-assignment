# Autoever Notification Assignment (Kotlin + Spring Boot + Redis)

현대오토에버 백엔드 과제로, 회원가입, 관리자 메시지 발송,  
TPS 제한 기반 비동기 알림 처리 시스템을 구현한 프로젝트입니다.

---

## 구현 범위

### 회원 기능

- 사용자 회원가입, 로그인
- 본인 정보 조회 (주소는 시/도 단위만 제공)

### 관리자 기능

- 연령대별 메시지 발송 요청 (관리자 API)
- Redis 기반 큐에 메시지 등록 (비동기 처리)
- 카카오톡 → 실패 시 SMS fallback 발송
- 카카오 100건/분, SMS 500건/분 TPS 제한 적용

---

## 실행 방법

### 1. 요구사항

- Java 17 이상
- Gradle (wrapper 포함)
- Kotlin 1.9+
- Spring Boot 3.5.4
- Docker (Redis 실행용)

### 2. Redis + Spring Boot 동시 실행

```bash
# Redis 실행 (Docker)
 docker compose up

# 애플리케이션 실행
./gradlew bootRun
```

> 서버는 기본적으로 `http://localhost:8080`에서 구동됩니다.

---

## 메시지 발송 흐름 (비동기)

1. `/admin/messages/send` 호출 시 사용자별 메시지를 Redis 큐에 적재
2. 내부 워커(`@Scheduled`)가 Redis 큐에서 하나씩 꺼냄
3. TPS 제한(RateLimiter)을 적용하며 카카오톡 전송 시도
4. 실패 시 SMS로 fallback 전송
5. 결과는 로그로 기록

---

## 테스트 실행 방법

```bash
./gradlew test
```

---

## H2 콘솔 접속 방법

### 접속 주소

```
http://localhost:8080/h2-console
```

### 설정 값

- Driver Class: `org.h2.Driver`
- JDBC URL: `jdbc:h2:mem:testdb`
- User Name: `sa`

---

## 초기 데이터

- `src/main/resources/data.sql`에서 다양한 연령대 샘플 사용자 자동 등록

---

## 전체 API CURL 예시

### 1. 회원가입

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "account": "tester1",
    "password": "secure123",
    "name": "홍길동",
    "rrn": "9601011234567",
    "phone": "01011112222",
    "address": "서울특별시 강남구"
  }'
```

---

### 2. 로그인

```bash
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "account": "tester1",
    "password": "secure123"
  }'
```

---

### 3. 본인 정보 조회

```bash
curl -X GET http://localhost:8080/api/users/me \
  -H "X-Account: tester1"
```

---

### 4. 관리자 - 회원 목록 조회 (페이징)

```bash
curl -X GET "http://localhost:8080/admin/users?page=0&size=10"
```

```bash
curl --location 'http://localhost:8080/admin/users?page=0&size=5' \
  --header 'Authorization: Basic YWRtaW46MTIxMg=='
```

---

### 5. 관리자 - 회원 정보 수정

```bash
curl -X PATCH http://localhost:8080/admin/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "address": "부산광역시 해운대구"
  }' \
  --header 'Authorization: Basic YWRtaW46MTIxMg=='
```

---

### 6. 관리자 - 회원 삭제

```bash
curl -X DELETE http://localhost:8080/admin/users/1 \
  --header 'Authorization: Basic YWRtaW46MTIxMg=='
```

---

### 7. 관리자 - 연령대별 메시지 발송 요청

```bash
curl -X POST http://localhost:8080/admin/messages/send \
  -H "Content-Type: application/json" \
  -d '{
    "10": "10대를 위한 첫차 혜택!",
    "20": "20대를 위한 모빌리티 할인!",
    "30": "30대를 위한 SUV 혜택!",
    "40": "40대를 위한 세단 무이자 행사!",
    "50": "50대를 위한 중고차 보상 프로모션!",
    "60": "60대를 위한 안락한 차량 추천!"
  }' \
  --header 'Authorization: Basic YWRtaW46MTIxMg=='
```

---

### 8. H2 콘솔 접속

웹 브라우저에서 아래 주소 접속:

```
http://localhost:8080/h2-console
```

- Driver Class: `org.h2.Driver`
- JDBC URL: `jdbc:h2:mem:testdb`
- User Name: `sa`
