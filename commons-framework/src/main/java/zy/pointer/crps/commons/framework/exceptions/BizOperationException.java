package zy.pointer.crps.commons.framework.exceptions;

import zy.pointer.crps.commons.framework.web.i18n.IErrorCode;

/**
 * 业务层异常
 */
public class BizOperationException extends RuntimeException {

    private IErrorCode code;

    private String extraMsg;

    public BizOperationException(IErrorCode code) {
        this.code = code;
    }

    public BizOperationException(IErrorCode code, String extraMsg) {
        this.code = code;
        this.extraMsg = extraMsg;
    }

    public IErrorCode getCode() {
        return code;
    }

    public void setCode(IErrorCode code) {
        this.code = code;
    }

    public String getExtraMsg() {
        return extraMsg;
    }

    public void setExtraMsg(String extraMsg) {
        this.extraMsg = extraMsg;
    }
}
