package com.nadia.config.meta.exception;

import com.nadia.config.common.exception.BaseException;

public class MetaException extends BaseException {
    public MetaException(String message){
        super(message);
    }

    public MetaException(Long errorCode){
        this("MetaException exception");
        setErrorCode(errorCode);
    }

    public MetaException(Long errorCode, Object[] args){
        super(errorCode,args);
    }
}
