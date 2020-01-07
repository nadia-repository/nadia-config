package com.nadia.config.user.exception;


import com.nadia.config.common.exception.BaseException;

public class AccountException extends BaseException {
    public AccountException(String message){
        super(message);
    }

    public AccountException(Long errorCode){
        this("Account exception");
        setErrorCode(errorCode);
    }

    public AccountException(Long errorCode, Object[] args){
        super(errorCode,args);
    }
}
