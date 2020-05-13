package org.run.base.exception;

/**
 * 自定义异常
 * @author koch
 *
 */
public class RRException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    private String msg;
    private int code = 500;
    private String errField;

    public RRException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public RRException(String msg,String errField) {
        super(msg);
        this.msg = msg;
        this.errField = errField;
    }

    public RRException(String msg, Throwable e) {
        super(msg, e);
        this.msg = msg;
    }

    public RRException(String msg, int code) {
        super(msg);
        this.msg = msg;
        this.code = code;
    }

    public RRException(String msg, int code, Throwable e) {
        super(msg, e);
        this.msg = msg;
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getErrField() {
        return errField;
    }

    public void setErrField(String errField) {
        this.errField = errField;
    }
}
