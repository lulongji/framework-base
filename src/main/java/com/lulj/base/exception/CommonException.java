package com.lulj.base.exception;

import com.lulj.base.constants.CommonConstants;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

/**
 * @author lu
 */
@Data
public class CommonException extends Exception {

    private static final long serialVersionUID = 2797300151072458673L;

    private String errCode = CommonConstants.CommonCode._STRING_1;

    private String errMsg;

    public CommonException() {
        super();
    }

    public CommonException(String errCode) {
        super("errCode:" + errCode);
        this.errCode = errCode;
    }

    public CommonException(String errCode, String errMsg) {
        super(errMsg);
        this.errCode = errCode;
        this.errMsg = errMsg;
    }


    public CommonException(Throwable cause) {
        super(cause);
        if (cause != null) {
            if (StringUtils.isNotEmpty(cause.getMessage())) {
                this.errMsg = cause.getMessage();
            } else {
                this.errMsg = cause.getLocalizedMessage();
            }

        }
    }

    public CommonException(String errCode, Throwable cause) {
        super(cause);
        this.errCode = errCode;
        if (cause != null) {
            if (StringUtils.isNotEmpty(cause.getMessage())) {
                this.errMsg = cause.getMessage();
            } else {
                this.errMsg = cause.getLocalizedMessage();
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{\"errCode\":\"");
        builder.append(errCode);
        builder.append("\", \"errMsg\":\"");
        builder.append(errMsg);
        builder.append("\"}");
        return builder.toString();
    }
}
