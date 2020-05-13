package org.run.util.result;

public class Result {
    protected static final Integer OK = 0;
    protected static final String OK_MSG = "success";
    protected static final Integer ERROR = 500;
    protected static final String ERROR_MSG = "error";
    public static final Integer RE_LOGIN = 300;
    public static final Integer CONTINUE_LOGIN = 400;
    private String message;
    private Integer code;
    private Object data;
    public static Result ok(Object data) {
        Result r = new Result();
        r.setData(data);
        r.setCode(OK);
        r.setMessage(OK_MSG);
        return r;
    }
    public static Result ok() {
        Result r = new Result();
        r.setCode(OK);
        r.setMessage(OK_MSG);
        return r;
    }

    public Result successMsg(String message) {
        Result r = 	Result.ok();
        r.setMessage(message);
        return r;
    }

    public static Result error() {
        Result r = 	new Result();
        r.setData(null);
        r.setCode(ERROR);
        r.setMessage(ERROR_MSG);
        return r;
    }

    public static Result error(String message) {
        Result r = Result.error();
        r.setMessage(message);
        return r;
    }


    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public Result setMessage(String message) {
        this.message = message;
        return this;
    }

    public Integer getCode() {
        return code;
    }

    public Result setCode(Integer code) {
        this.code = code;
        return this;
    }
}
