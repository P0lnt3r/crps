package zy.pointer.crps.commons.framework.web.model;

import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import zy.pointer.crps.commons.framework.web.i18n.IErrorCode;


@Component
public class JSONResponseHelper {

    @Autowired
    zy.pointer.crps.commons.framework.web.i18n.I18N I18N;

    public JSONResponse success(Object data ){
        return JSONResponse.Build( IErrorCode.SUCCESS_CODE , I18N.getI18NMessage( IErrorCode.SUCCESS_CODE ) , data );
    }

    public JSONResponse error ( IErrorCode errorCode ){
        return error(errorCode , "");
    }

    public JSONResponse error( IErrorCode errorCode , String extras ){
        String message = I18N.getI18NMessage( errorCode.code() );
        if (StrUtil.isNotEmpty(extras)){
            message += ":" + extras;
        }
        return JSONResponse.Build( errorCode.code() , message , "" );
    }

}
