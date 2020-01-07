package com.nadia.config.common.exception;

public class BusinessException extends BaseException {

	public BusinessException(String message){
        super(message);
    }

    public BusinessException(Long errorCode){
        this("BusinessException exception");
        setErrorCode(errorCode);
    }

    public BusinessException(Long errorCode, Object[] args){
        super(errorCode,args);
    }
}
