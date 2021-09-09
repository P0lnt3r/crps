package zy.pointer.crps.commons.framework.web;


import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import zy.pointer.crps.commons.framework.exceptions.BizOperationException;
import zy.pointer.crps.commons.framework.web.i18n.ErrorCode;
import zy.pointer.crps.commons.framework.web.i18n.IErrorCode;
import zy.pointer.crps.commons.framework.web.model.JSONResponse;
import zy.pointer.crps.commons.framework.web.model.JSONResponseHelper;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
@Slf4j
public class ControllerAdvisor implements ResponseBodyAdvice {

    @Autowired
    JSONResponseHelper jsonResponseHelper;

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return returnType.getDeclaringClass().getDeclaredAnnotation(RestController.class) != null ;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        return jsonResponseHelper.success(body);
    }

    @ExceptionHandler(BizOperationException.class)
    public JSONResponse handleBusinessException(BizOperationException exception ){
        IErrorCode errorCode = exception.getCode();
        return jsonResponseHelper.error(errorCode);
    }

    @ExceptionHandler(BindException.class)
    public JSONResponse handleBindException( BindException bindExcetion){
        IErrorCode errorCode = ErrorCode.VALIDATE_EXCEPTION;
        FieldError fieldError = bindExcetion.getFieldError();
        bindExcetion.printStackTrace();
        return jsonResponseHelper.error( errorCode , fieldError.getDefaultMessage());
    }

    @ExceptionHandler(Exception.class)
    public JSONResponse handleException( Exception exception ){
        exception.printStackTrace();
        IErrorCode errorCode = ErrorCode.EXCEPTION;
        return jsonResponseHelper.error(errorCode);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public Object handle405Exception(Exception e , HttpServletRequest request){
        return jsonResponseHelper.error(ErrorCode.SYS_ERR_405);
    }

}
