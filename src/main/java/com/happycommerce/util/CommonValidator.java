package com.happycommerce.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

/**
 * Spring validator 활용 목적.
 * validator Exception 발생 시 Custom exception 저장을 위함.
 * @param <T>
 */
@Slf4j
@Component
public class CommonValidator<T> {
    private final Validator validator;

    public CommonValidator(Validator validator) {
        this.validator = validator;
    }

    public void validate(T body) {
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(body);
        if (!constraintViolations.isEmpty()) {
            onValidationErrors(constraintViolations);
        }
    }

    private void onValidationErrors(Set<ConstraintViolation<T>> constraintViolations) {
        ConstraintViolation<T> firstViolation = constraintViolations.iterator().next();
        String propertyPath = firstViolation.getPropertyPath().toString();
        String message = firstViolation.getMessage();

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "propertyPath=" + propertyPath + ", message=" + message);
    }
}
