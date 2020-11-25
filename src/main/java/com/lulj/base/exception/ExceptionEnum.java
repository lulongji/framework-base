package com.lulj.base.exception;


import com.lulj.base.constants.CommonConstants;

/**
 * @Description: 统一异常错误码 4位 (base 包)
 * @Author: lu
 * @Date: Created in 10:09 2018/9/29
 */
public enum ExceptionEnum {
    /**
     * An unknown error.
     */
    UNKONW_ERROR(CommonConstants.ValType.UNKNOWN_ERROR, "An unknown error."),
    UNIQUE_ERROR(CommonConstants.ValType.UNIQUE_ERROR, "An unique error."),


    JSON_CONVERTED_EXCEPTION_1001("1001", "An error occurred when the entity converted JSON！"),

    /**
     * 成功
     */
    SUCCESS(CommonConstants.ValType.SUCCESS_CODE, CommonConstants.ValType.FAILURE_CODE),
    ;

    private String code;

    private String info;

    ExceptionEnum(String code, String info) {
        this.code = code;
        this.info = info;
    }

    public String getCode() {

        return code;
    }

    public String getInfo() {
        return info;
    }
}