package com.lulj.base.bean;

import com.lulj.base.constants.CommonConstants;

import java.io.Serializable;


/**
 * Return result set.
 *
 * @author lu
 */
public class Result implements Serializable {

    private static final long serialVersionUID = 1L;
    private String code;
    private String info;
    private Object result;

    public static Result success() {
        Result result = new Result();
        result.setCode(CommonConstants.ValType.SUCCESS_CODE);
        result.setInfo(CommonConstants.ValType.SUCCESS_INFO);
        return result;
    }

    public static Result success(Object result) {
        Result res = new Result(CommonConstants.ValType.SUCCESS_CODE, CommonConstants.ValType.SUCCESS_INFO);
        res.setResult(result);
        return res;
    }

    public static Result failure() {
        Result result = new Result();
        result.setCode(CommonConstants.ValType.FAILURE_CODE);
        result.setInfo(CommonConstants.ValType.FAILURE_INFO);
        return result;
    }

    public static Result failure(String info) {
        Result result = failure();
        result.setInfo(info);
        return result;
    }

    public static Result error(String code, String info) {
        Result result = new Result();
        result.setCode(code);
        result.setInfo(info);
        return result;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    private Result(String code, String info, Object result) {
        super();
        this.code = code;
        this.info = info;
        this.result = result;
    }

    private Result(String code, String info) {
        super();
        this.code = code;
        this.info = info;
    }

    private Result() {
        super();
    }
}
