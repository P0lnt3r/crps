package zy.pointer.crps.commons.framework.web.i18n;

public enum ErrorCode implements IErrorCode {

    // 成功
    SUCCESS( IErrorCode.SUCCESS_CODE , IErrorCode.SUCCESS_DESC ) ,
    EXCEPTION( "10000" , "系统繁忙,请稍后再试" ),
    SYS_ERR_405( "10405" , "访问方法不支持" ),
    VALIDATE_EXCEPTION(                             "1000" , "参数错误" ),
    NULL( "","" );

    private ErrorCode(String code , String desc ){
        this.code = code;
        this.desc = desc;
    }
    public String code ,desc ;

    @Override
    public String code() {
        return code;
    }

    @Override
    public String desc() {
        return desc;
    }
}
