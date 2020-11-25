package com.lulj.base.exception;


/**
 * @Description: 自定义异常
 * @Author: lu
 * @Date: Created in 10:08 2018/9/29
 */
public class BootException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private String code;

    public BootException(ExceptionEnum exceptionEnum) {
        super(exceptionEnum.getInfo());
        this.code = exceptionEnum.getCode();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
