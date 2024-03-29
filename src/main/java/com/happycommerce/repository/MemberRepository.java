package com.happycommerce.repository;

import com.happycommerce.entity.Member;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;


@Repository
public interface MemberRepository extends R2dbcRepository<Member, Long> {
    @Query("SELECT * FROM t_member WHERE mbrno=:mbrNo AND access_token IS NOT NULL AND access_token_expired_date > now()")
    Mono<Member> findByValidatedTokenMember(String mbrNo);

    @Query("SELECT * FROM t_member WHERE login_token=:loginToken")
    Mono<Member> findByValidatedLoginTokenMember(String loginToken);

    Mono<Member> findByMbrNo(String mbrNo);
}
