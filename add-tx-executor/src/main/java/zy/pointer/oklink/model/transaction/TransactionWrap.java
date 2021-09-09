package zy.pointer.oklink.model.transaction;

import lombok.Data;

import java.util.List;

@Data
public class TransactionWrap<T> {

    private Integer total;

    private List<T> hits;

}
