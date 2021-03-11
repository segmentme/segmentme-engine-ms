package io.segmentme.web.configuration;

import io.segmentme.core.dto.error.ErrorMessage;
import io.segmentme.core.dto.error.ErrorType;
import io.segmentme.core.dto.error.SimpleErrorDto;
import io.segmentme.core.dto.error.ValidationErrorDto;
import io.segmentme.models.shared.exception.AbstractManagerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;


@Slf4j
@Order
@ControllerAdvice
public class CommonExceptionAdviceController {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    @ExceptionHandler(RuntimeException.class)
    public ErrorMessage handleRuntimeError(RuntimeException ex) {
        log.error("Application Runtime Exception", ex);
        return new SimpleErrorDto(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    @ExceptionHandler(AbstractManagerException.class)
    public ErrorMessage handleRuntimeError(AbstractManagerException ex) {
        log.error("manager Runtime Exception", ex);
        return new SimpleErrorDto(ErrorType.SERVICE_ERROR, ex.getCode());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    @ExceptionHandler(AccessDeniedException.class)
    public ErrorMessage handleAccessDeniedError(AccessDeniedException ex) {
        log.warn("Access Denied Exception", ex);
        return new SimpleErrorDto(ErrorType.AUTHENTICATION_ERROR, ex.getMessage());
    }

//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ResponseBody
//    @ExceptionHandler(ConstraintViolationException.class)
//    public ErrorMessage invalidParamsExceptionHandler(ConstraintViolationException ex) {
//        log.warn("ConstraintViolationException: {}", ex.getMessage());
//        return resolveConstraintViolations(ex.getConstraintViolations());
//    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler
    public ValidationErrorDto handleValidationError(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        List<ObjectError> errors = result.getAllErrors();
        return processErrors(errors);
    }

//    private ValidationErrorDto resolveConstraintViolations(Collection<ConstraintViolation<?>> constraintViolations) {
//        ValidationErrorDto dto = new ValidationErrorDto(ErrorType.VALIDATION_ERROR);
//        constraintViolations.forEach(cv -> {
//            String parameter = getParameterName(cv);
//            dto.add(parameter, parameter, cv.getMessage());
//        });
//        return dto;
//    }

    private ValidationErrorDto processErrors(List<ObjectError> errors) {
        ValidationErrorDto dto = new ValidationErrorDto(ErrorType.VALIDATION_ERROR);
        for (ObjectError error : errors) {
            if (error instanceof FieldError) {
                dto.add(error.getObjectName(), ((FieldError) error).getField(), error.getDefaultMessage());
            } else {
                dto.add(error.getObjectName(), null, error.getDefaultMessage());
            }
        }
        return dto;
    }
//
//    private String getParameterName(ConstraintViolation<?> constraintViolation) {
//        return StreamSupport.stream(constraintViolation.getPropertyPath().spliterator(), false)
//            .filter(p -> p.getKind().equals(ElementKind.PARAMETER))
//            .findFirst()
//            .map(Path.Node::getName)
//            .orElse(null);
//    }
}
