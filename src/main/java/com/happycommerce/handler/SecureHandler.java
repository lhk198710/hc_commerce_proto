package com.happycommerce.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.happycommerce.api.HPCWebClientFactory;
import com.happycommerce.common.CommonWebClient;
import com.happycommerce.dto.*;
import com.happycommerce.entity.ProxyApiKey;
import com.happycommerce.repository.ProxyApiKeyRepository;
import com.happycommerce.util.CommonUtil;
import com.happycommerce.util.CommonValidator;
import com.happycommerce.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Component
public class SecureHandler {
    private final ProxyApiKeyRepository proxyApiKeyRepository; // R2DBC 레포지토리
    private final TransactionalOperator operator; // 트랜잭션 처리
    private final CommonValidator commonValidator; // Spring validator
    private final CommonWebClient commonWebClient; // HTTPS 통신
    private final HPCWebClientFactory hpcWebClientFactory; // FOR HPC

    public SecureHandler(ProxyApiKeyRepository proxyApiKeyRepository, TransactionalOperator operator, CommonValidator commonValidator, CommonWebClient commonWebClient, HPCWebClientFactory hpcWebClientFactory) {
        this.proxyApiKeyRepository = proxyApiKeyRepository;
        this.operator = operator;
        this.commonValidator = commonValidator;
        this.commonWebClient = commonWebClient;
        this.hpcWebClientFactory = hpcWebClientFactory;
    }

    public Mono<ServerResponse> testHandler(ServerRequest request) {
        log.info("Received request: /api/v1/secure");
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(new TestResponseDto("API KEY 인증 필요한 라우터 테스트")));
    }

    public Mono<ServerResponse> testHandler2(ServerRequest request) {
        log.info("Received request: /api/admin/v1/get");
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(new TestResponseDto("API KEY 인증 필요한 라우터 테스트. ROLE_ADMIN 권한만 가능.")));
    }

    /**
     * insert 테스트 목적
     * @param request HTTP(S) Body data
     * @return Mono<ServerResponse>
     */
    public Mono<ServerResponse> testRequestDataInsert(ServerRequest request) {
        log.info("Received request: /api/admin/v1/dto");
        return request.bodyToMono(TestRequestDto.class)
                .doOnNext(testRequestDto -> log.info("stream start : {}", testRequestDto))
                .doOnNext(testRequestDto -> commonValidator.validate(testRequestDto))
                .map(testRequestDto -> ProxyApiKey.builder()
                        .apiKey(testRequestDto.getApiKey())
                        .role(testRequestDto.getRole())
                        .seq(null)
                        .regId(testRequestDto.getRegId())
                        .updId(testRequestDto.getUpdId())
                        .clientName(testRequestDto.getClientName())
                        .clientCode(testRequestDto.getClientCode())
                        .useYn("Y")
                        .build())
                .flatMap(proxyApiKeyRepository::save) // subscribe 사용해도 입력은 가능하다만 트랜잭션 처리와 관련 없음. 트랜잭션 내에서 비동기 작업 처리하려면 flatMap 또는 concatMap 을 사용해 Mono 또는  Flux 반환 필요
                .flatMap(data -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(new TestResponseDto("API KEY 인증 필요한 라우터 테스트. ROLE_ADMIN 권한만 가능.. RequestBody Input Test"))))
                .doOnError(ex -> log.error("testRequestDataInsert exception : ", ex))
                .as(operator::transactional)
                ;
    }

    /**
     * Transaction 테스트 목적
     * - map 에서 Entity 만들고 응답 후 FlatMap 에서 Repository Sve 사용할 것. Mono 또는 Flux 로 넘기는 것이 중요함.
     * @param request HTTP(S) Body data
     * @return Mono<ServerResponse>
     */
    public Mono<ServerResponse> testRequestDataInsertWithRollback(ServerRequest request) {
        log.info("Received request: /api/admin/v1/dto/rollback");
        return request.bodyToMono(TestRequestDto.class)
                .doOnNext(testRequestDto -> log.info("stream start : {}", testRequestDto))
                .map(testRequestDto -> ProxyApiKey.builder()
                        .apiKey(testRequestDto.getApiKey())
                        .role(testRequestDto.getRole())
                        .seq(null)
                        .regId(testRequestDto.getRegId())
                        .updId(testRequestDto.getUpdId())
                        .clientName(testRequestDto.getClientName())
                        .clientCode(testRequestDto.getClientCode())
                        .useYn("Y")
                        .build())
                .flatMap(proxyApiKeyRepository::save) // subscribe 사용해도 입력은 가능하다만 트랜잭션 처리와 관련 없음. 트랜잭션 내에서 비동기 작업 처리하려면 flatMap 또는 concatMap 을 사용해 Mono 또는 Flux 반환 필요
                .doOnNext(data -> {throw new IllegalArgumentException("Test exception");})
                .flatMap(data -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(new TestResponseDto("API KEY 인증 필요한 라우터 테스트. ROLE_ADMIN 권한만 가능.. RequestBody Input Test"))))
                .doOnError(ex -> log.error("testRequestDataInsertWithRollback exception : ", ex)) // 향후 이 영역에서 Exception 잡은 후 응답 데이터 만들 필요 있음.
                .as(operator::transactional)
                ;
    }

    /**
     * HPC Communicate test
     * - map 안에 map 사용건에 대한 개선 조언을 기반으로 수정
     * - flatMap : DB 작업, 통신 작업 위주 진행
     * - map : 기타 작업 진행
     * @param request
     * @return
     */
    public Mono<ServerResponse> testHPCComm(ServerRequest request) {
        log.info("Received request: /api/admin/v1/hpc/get");
        LocalDateTime now = LocalDateTime.now();

        return request.bodyToMono(AuthRequestDto.class)
                .doOnNext(authRequestDto -> log.info("stream start : {}, traceId : {}", authRequestDto, CommonUtil.getTraceId(request)))
                .map(authRequestDto -> HPCAuthRequestDto.builder()
                        .trsDt(StringUtil.convertDateToString(now, "yyyyMMdd"))
                        .trsTm(StringUtil.convertDateToString(now, "HHmmss"))
                        .tracNo(CommonUtil.getTraceId(request))
                        .reqUserId(authRequestDto.getHpcId())
                        .onlnId(authRequestDto.getHpcId())
                        .onlnPwd(authRequestDto.getHpcPwd())
                        .build())
                .flatMap(hpcAuthRequestDto -> commonWebClient.sendDataPostApplicationJson(hpcWebClientFactory.getClient(),
                        "https://xxx-auth.com/test",
                        hpcAuthRequestDto, HPCAuthResponseDto.class, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, "test"))
                .map(hpcAuthResponseDto -> {
                    try {
                        return hpcAuthResponseDto.hpcAuthResponseOf(); // 나중에 ResponseDTO로 바꿀 것
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .flatMap(result -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(result)
                        .doOnError(ex -> log.error("testHPCComm exception : ", ex)))
                ;
    }
}
