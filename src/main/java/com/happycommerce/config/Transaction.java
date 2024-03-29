package com.happycommerce.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * For Test
 * 트랜잭션 테스트를 목적으로 생성한 클래스로 서비스에서는 사용 할 일 없음.
 */
@Deprecated
@Component
public class Transaction {
    private static TransactionalOperator operator;

    @Autowired
    protected Transaction(final TransactionalOperator operator) {
        Transaction.operator = operator;
    }

    public static <T> Mono<T> withRollBack(final Mono<T> publisher){
        return operator.execute(tx->{
            tx.setRollbackOnly();
            return publisher;
        }).next();
    }

    public static <T> Flux<T> withRollBack(final Flux<T> publisher){
        return operator.execute(tx->{
            tx.setRollbackOnly();
            return publisher;
        });
    }
}