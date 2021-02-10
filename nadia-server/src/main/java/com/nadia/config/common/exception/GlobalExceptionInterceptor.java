package com.nadia.config.common.exception;

import com.nadia.config.common.rest.RestBody;
import com.nadia.config.common.rest.RestHeader;
import com.nadia.config.common.util.I18nUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@ControllerAdvice
public class GlobalExceptionInterceptor {

    @ResponseBody
    @ExceptionHandler(Throwable.class)
    public RestHeader handleException(Throwable e){
        String requestPath= "";
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        if(requestAttributes!= null){
            HttpServletRequest request = requestAttributes.getRequest();
            if(request!= null){
                requestPath= request.getRequestURI();
            }
        }
        if(e instanceof BaseException){
            RestHeader response=  ((BaseException)e).getExceptionResponse();
            log.warn("Biz error: {},request path: {}",response,requestPath);
            return response;
        }
        //invalid arguments
        if(e instanceof MethodArgumentNotValidException){
            RestHeader response= new RestHeader();
            response.setErrorCode(BaseException.INVALID_ARGUMENTS_ERROR_CODE);
            BindingResult result= ((MethodArgumentNotValidException)e).getBindingResult();
            FieldError fieldError= result.getFieldError();
            String defaultMessage= fieldError.getDefaultMessage();
            String msg= StringUtils.isEmpty(defaultMessage)?
                    (!StringUtils.isEmpty(fieldError.getCode()) ?
                            fieldError.getCode() : e.getMessage())
                    : (defaultMessage.startsWith(I18nUtils.I18N_PREFIX)?
                            I18nUtils.getMessage(defaultMessage.substring(I18nUtils.I18N_PREFIX.length()),fieldError.getCode())
                            :defaultMessage);
            response.setMsg(msg);
            log.warn("Invalid arguments error: {},request path: {}",response,requestPath);
            return response;
        }
        //invalid conversion
        if(e instanceof HttpMessageConversionException){
            RestHeader response= new RestHeader();
            response.setErrorCode(BaseException.INVALID_CONVERSION_ERROR_CODE);
            int idx= e.getMessage().indexOf("nested exception");
            if(idx>0){
                response.setMsg(e.getMessage().substring(0,idx));
            }
            else {
                response.setMsg(e.getMessage());
            }
            log.error("Invalid conversion error: {},request path: {}",response,requestPath,e);
            return response;
        }
        //default
        RestHeader response= new RestHeader();
        response.setErrorCode(BaseException.DEFAULT_ERROR_CODE);
        if(e instanceof NullPointerException){
            response.setMsg((e.getStackTrace()!=null && e.getStackTrace().length>0)?("NullPointerException: "+e.getStackTrace()[0].toString()):e.getMessage());
        }
        else {
            response.setMsg(e.getMessage());
        }
        log.error("System error: {},request path: {}",response,requestPath,e);
        return response;
    }

}
