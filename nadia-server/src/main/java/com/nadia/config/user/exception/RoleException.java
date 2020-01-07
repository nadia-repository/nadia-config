package com.nadia.config.user.exception;

import com.nadia.config.common.exception.BaseException;

/**
 * @author xiang.shi
 * @date 2019-12-11 19:20
 */
public class RoleException extends BaseException {
    public RoleException(String message){
        super(message);
    }

    public RoleException(Long errorCode){
        this("Role exception");
        setErrorCode(errorCode);
    }

    public RoleException(Long errorCode, Object[] args){
        super(errorCode,args);
    }
}
