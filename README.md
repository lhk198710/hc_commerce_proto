# WebFlux 기반 API 연동을 위한 Project

### Library

프로젝트 연동을 위해 사용하거나 참고한 lib 명시:

* SpringBoot 2.7.15 & Netty
* OpenJDK 11
* R2dbc : Mysql 
* Spring Security
* Spring WebFlux
* Caffein Cache
* ETC : lombok, JUnit5, Swagger

### 사용 목적

해당 프로젝트 통해 연동하고자 했던 방향에 대해 명시한다:

* WebFlux를 이용한 비동기 방식 API Project 구축
* WebFlux에서 추구하는 비동기 방식 서비스 제공을 위한 R2DBC 연동 및 트랜잭션 기능 구현
* WebFlux + Spring security 기반 HTTP(S) Header 인증 프로토콜 구현 및 인증 키 데이터 캐시 관리
* WebFlux + Swagger 기반 JavaOpenDoc 기능 제공

### HTTPS 통신 방식

CommonWebClient.java 기반, 통신을 진행해야 하는 서버가 여러대인 경우 대비하여 별도 설정을 하는 방향으로 구현

* WebClientFactory 구현을 통해 Timeout, MediaType 등 설정
* 위 구현체를 이용, 각각의 서버와 통신을 진행하는 CommonWebClient.send*** 함수 실행


### API 인증 방식

WebFlux + Spring security를 이용한 API 인증 방식에 대하여 기술한다:

* HTTP Request Header 내 Api-Key를 DB 또는 내장 캐시 데이터와 비교하여 유효성 검증
* 특정 Path로 명명된 EndPoint(Router)에 대해서만 Api-Key를 이용해 데이터 비교
* 특정 Path의 경우 Api-Key와 더불어 DB 또는 내장 캐시에 존재하는 사용 권한도 같이 비교

### 에러 처리 방식

GlobalErrorExceptionHandler 기반 프로젝트 내에서 발생하는 Exception들 공통으로 관리하는 것을 목표로 연동.

* 단, SecurityConfiguration에서 발생하는 인증 & 인가 에러에 대해서는 별도 처리를 하였으나 위 목표를 위해 공통 DTO 사용.
* 기타 에러 발생건에 대해서는 ErrorMessage Object 기반 응답 표기.


### Key 관리 방식
DB 내 Key 관리를 기본으로 Caffein cache를 통해 한번 조회 된 데이터들에 대해서는 특정 기간동안 캐시화 하여 보관

* 캐시 만료 시간 : 1분(별도 설정 가능)
* 상세 구현 내용은 KeyAuthenticationConvert.java 참고