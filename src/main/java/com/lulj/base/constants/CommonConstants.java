package com.lulj.base.constants;

/**
 * @author lu
 * @date 2018/12/17
 */
public class CommonConstants {

    /**
     * 通用返回体code
     */
    public static class ValType {

        public static final String SUCCESS_INFO = "success";

        public static final String SUCCESS_CODE = "200";

        public static final int SUCC = 200;

        public static final String FAILURE_INFO = "failure";

        public static final String FAILURE_CODE = "500";

        public static final int FAILURE = 500;

        /*全局异常错误码*/
        public static final String UNKNOWN_ERROR = "-1";

        /*违反唯一约束*/
        public static final String UNIQUE_ERROR = "-2";

    }

    /**
     * 通用常量数据
     */
    public static class CommonCode {

        public static final String _STRING_1 = "-1";
        public static final String _STRING_2 = "-2";
        public static final String _STRING_3 = "-3";
        public static final String _STRING_4 = "-4";
        public static final String STRING_0 = "0";
        public static final String STRING_1 = "1";
        public static final String STRING_2 = "2";
        public static final String STRING_3 = "3";
        public static final String STRING_4 = "4";
        public static final Integer INTEGER_0 = 0;
        public static final Integer INTEGER_1 = 1;
        public static final Integer INTEGER_2 = 2;
        public static final Integer INTEGER_3 = 3;
        public static final Integer INTEGER_4 = 3;
        public static final Integer _INTEGER_1 = -1;
        public static final Integer _INTEGER_2 = -2;
        public static final Integer _INTEGER_3 = -3;
        public static final Integer _INTEGER_4 = -3;

    }

    /**
     * sql唯一约束异常
     */
    public static final String SQL_INTEGRITY_CONSTRAINT_VIOLATION_EXCEPTION = "java.sql.SQLIntegrityConstraintViolationException";
    /**
     * UTF-8 字符集
     */
    public static final String UTF8 = "UTF-8";

    /**
     * GBK 字符集
     */
    public static final String GBK = "GBK";

    /**
     * http请求
     */
    public static final String HTTP = "http://";

    /**
     * https请求
     */
    public static final String HTTPS = "https://";


    /**
     * 验证码 redis key
     */
    public static final String CAPTCHA_CODE_KEY = "captcha_codes:";

    /**
     * 登录用户 redis key
     */
    public static final String LOGIN_TOKEN_KEY = "login_tokens:";

    /**
     * 防重提交 redis key
     */
    public static final String REPEAT_SUBMIT_KEY = "repeat_submit:";

    /**
     * 验证码有效期（分钟）
     */
    public static final Integer CAPTCHA_EXPIRATION = 2;

    /**
     * 令牌
     */
    public static final String TOKEN = "token";

    /**
     * 令牌前缀
     */
    public static final String TOKEN_PREFIX = "ytx ";

    /**
     * 令牌前缀
     */
    public static final String LOGIN_USER_KEY = "login_user_key";

    /**
     * 用户ID
     */
    public static final String JWT_USERID = "userid";

    /**
     * 用户名称
     */
    public static final String JWT_USERNAME = "sub";

    /**
     * 用户头像
     */
    public static final String JWT_AVATAR = "avatar";

    /**
     * 创建时间
     */
    public static final String JWT_CREATED = "created";

    /**
     * 用户权限
     */
    public static final String JWT_AUTHORITIES = "authorities";

    /**
     * 参数管理 cache key
     */
    public static final String SYS_CONFIG_KEY = "sys_config:";

    /**
     * 字典管理 cache key
     */
    public static final String SYS_DICT_KEY = "sys_dict:";

    /**
     * 资源映射路径 前缀
     */
    public static final String RESOURCE_PREFIX = "/profile";

}
