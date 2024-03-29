package com.happycommerce.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.happycommerce.api.HPCWebClientFactory;
import com.happycommerce.common.CommonWebClient;
import com.happycommerce.dto.*;
import com.happycommerce.entity.Member;
import com.happycommerce.entity.ProxyApiKey;
import com.happycommerce.exception.CommonApiException;
import com.happycommerce.repository.MemberRepository;
import com.happycommerce.repository.ProxyApiKeyRepository;
import com.happycommerce.util.CommonUtil;
import com.happycommerce.util.CommonValidator;
import com.happycommerce.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
public class SecureHandler {
    @Value("${api.hpc.url}")
    private String hpcUrl;
    private final ProxyApiKeyRepository proxyApiKeyRepository; // R2DBC 레포지토리
    private final TransactionalOperator operator; // 트랜잭션 처리
    private final CommonValidator commonValidator; // Spring validator
    private final CommonWebClient commonWebClient; // HTTPS 통신
    private final HPCWebClientFactory hpcWebClientFactory; // FOR HPC
    private final MemberRepository memberRepository;

    public SecureHandler(ProxyApiKeyRepository proxyApiKeyRepository, TransactionalOperator operator, CommonValidator commonValidator, CommonWebClient commonWebClient, HPCWebClientFactory hpcWebClientFactory, MemberRepository memberRepository) {
        this.proxyApiKeyRepository = proxyApiKeyRepository;
        this.operator = operator;
        this.commonValidator = commonValidator;
        this.commonWebClient = commonWebClient;
        this.hpcWebClientFactory = hpcWebClientFactory;
        this.memberRepository = memberRepository;
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
     * 회원 서버 Communicate test
     * - API 서버 통신 후 db 작업에 대한 학습을 목적으로 정의 된 함수다.
     * - 본 코드에서 정의된 API 서버는 실존하는 서버가 아님을 명시한다.
     * @param request
     * @return
     */
    public Mono<ServerResponse> testHPCComm(ServerRequest request) {
        final String HPC_RPS_SUCCESS_CODE = "00";
        final String HPC_RPS_DTL_SUCCESS_CODE = "0000";

        log.info("Received request: /api/admin/v1/hpc/get");
        LocalDateTime now = LocalDateTime.now();

        return request.bodyToMono(AuthRequestDto.class)
                .doOnNext(authRequestDto -> log.info("stream start : {}, traceId : {}", authRequestDto, CommonUtil.getTraceId(request)))
                .doOnNext(testRequestDto -> commonValidator.validate(testRequestDto))
                .map(authRequestDto -> HPCAuthRequestDto.builder()
                        .trsDt(StringUtil.convertDateToString(now, "yyyyMMdd"))
                        .trsTm(StringUtil.convertDateToString(now, "HHmmss"))
                        .tracNo(CommonUtil.getTraceId(request))
                        .reqUserId(authRequestDto.getHpcId())
                        .onlnId(authRequestDto.getHpcId())
                        .onlnPwd(authRequestDto.getHpcPwd())
                        .build())
                .flatMap(hpcAuthRequestDto -> commonWebClient.sendDataPostApplicationJson(hpcWebClientFactory.getClient(),
                        hpcUrl,
                        hpcAuthRequestDto, HPCAuthResponseDto.class, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, CommonUtil.getTraceId(request)))
                .flatMap(hpcApiAuthResponseDto -> {
                    if (hpcApiAuthResponseDto.getRpsCd().equals(HPC_RPS_SUCCESS_CODE) && hpcApiAuthResponseDto.getRpsDtlCd().equals(HPC_RPS_DTL_SUCCESS_CODE)) {
                        return memberRepository.findByMbrNo(hpcApiAuthResponseDto.getMbrNo())
                                .subscribeOn(Schedulers.boundedElastic())
                                .timeout(Duration.ofMillis(3000), Mono.error(new TimeoutException("memberRepository.findByMbrNo() timeout")))
                                .flatMap(existingMember -> {
                                    existingMember.setLoginToken(CommonUtil.generateLoginToken());
                                    return Mono.just(existingMember);
                                })
                                .switchIfEmpty(Mono.defer(() -> {
                                    Member member = Member.builder()
                                            .mbrNo(hpcApiAuthResponseDto.getMbrNo())
                                            .id(hpcApiAuthResponseDto.getOnlnId())
                                            .name(hpcApiAuthResponseDto.getMbrNm())
                                            .hpcCardNo(hpcApiAuthResponseDto.getCardNo())
                                            .loginToken(CommonUtil.generateLoginToken())
                                            .build();

                                    return Mono.just(member);
                                }))
                                ;
                    } else {
                        return Mono.error(new CommonApiException(hpcApiAuthResponseDto.getRpsDtlCd(), hpcApiAuthResponseDto.getRpsDtlMsg()));
                    }
                })
                .flatMap(memberRepository::save) // save 또는 업데이트(mbrNo 존재 기준)
                .map(data -> {
                    // 성공
                    return ClientResponseDto.builder()
                            .status(HttpStatus.OK.value())
                            .data(new HashMap() {
                                {
                                    put("loginToken", data.getLoginToken());
                                }
                            })
                            .build();
                })
                .flatMap(result -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(result)
                        .doOnError(ex -> log.error("webLogin exception : ", ex)))
                .as(operator::transactional)
                ;
    }
}
