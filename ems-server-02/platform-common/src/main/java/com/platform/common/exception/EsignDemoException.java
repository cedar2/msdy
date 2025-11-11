package com.platform.common.exception;

/**
 * description 自定义全局异常
 *
 * @author xfzz
 */
public class EsignDemoException extends RuntimeException {

    private static final long serialVersionUID = 4359180081622082792L;
    private RuntimeException e;

    public EsignDemoException(String msg) {
        super(msg);
    }

    public EsignDemoException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public EsignDemoException() {

    }

    public RuntimeException getE() {
        return e;
    }

    public void setE(RuntimeException e) {
        this.e = e;
    }

}
