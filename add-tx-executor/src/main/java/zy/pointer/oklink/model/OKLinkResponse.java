package zy.pointer.oklink.model;

import lombok.Data;

@Data
public class OKLinkResponse<T> {

    private Integer code ;

    private String msg;

    private String detailMsg;

    private T data;

}
