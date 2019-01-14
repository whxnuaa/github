package com.jit.skiad.commons;



public enum ResultCode {

    SUCCESS("SUCCESS", "00000"),//成功

    USER_ISEXITE("该用户名已存在!", "20001"),
    USER_LOGIN_ERROR("账号不存在或密码错误", "20002"),
    USER_ISERROR("用户名错误", "20003"),
    NUMBER_ISERROR("工号错误","20004"),
    ADMIN_RESET_DENYED("管理员不能重置密码","20005"),


    /* 业务错误：30001-39999 */
    SPECIFIED_QUESTIONED_USER_NOT_EXIST("某业务出现问题", "30001"),

    /* 系统错误：40001-49999 */
    SYSTEM_INNER_ERROR("系统繁忙，请稍后重试", "40001"),

    /* 数据错误：50001-599999 */
    RESULE_DATA_NONE("数据未找到", "50001"),
    DATA_IS_WRONG("数据有误", "50002"),
    DATA_ALREADY_EXISTED("数据已存在", "50003"),
    TOKEN_NOT_PROVID("Token 不存在","50004"),
    TOKEN_FORMAT_ERROR("Token 格式错误","50004"),
    TOKEN_EXPIRED("Token 过期","50004"),
    TOKEN_INVALID("无效 token","50004"),
    PASSWORD_SAME("与原密码重复","50005"),
    PASSWORD_IS_ERROR("原密码错误","50006"),
    Parameter_IS_NULL("参数为空","50007"),
    DEVICE_NOT_EXIST("数据库中不存在此设备","50008"),
    PARAMETER_IS_WRONG("请求参数有误","50009"),



    /* 接口错误：60001-69999 */
    INTERFACE_INNER_INVOKE_ERROR("内部系统接口调用异常", "60001"),
    INTERFACE_OUTTER_INVOKE_ERROR("外部系统接口调用异常", "60002"),
    INTERFACE_FORBID_VISIT("该接口禁止访问", "60003"),
    INTERFACE_ADDRESS_INVALID("接口地址无效", "60004"),
    INTERFACE_REQUEST_TIMEOUT("接口请求超时", "60005"),
    INTERFACE_EXCEED_LOAD("接口负载过高", "60006"),

    GATEWAY_NOT_ON_LINE("网关不在线","60007"),
    GATEWAY_NOT_EXIST("网关不存在","60007"),
    MINA_SENDMESSAGE_ERROR("mina服务连接失败","60008"),
    MINA_RECEIVE_DATA_ERROR("mina接收数据失败","60009"),

    /* 权限错误：70001-79999 */
    PERMISSION_NO_ACCESS("无访问权限", "70001"),
    NO_LOGIN("请登录","70002");

    private String message;
    private String code;
    ResultCode(String message, String code){
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
