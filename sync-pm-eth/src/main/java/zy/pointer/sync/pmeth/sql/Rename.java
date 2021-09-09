package zy.pointer.sync.pmeth.sql;

import zy.pointer.crps.commons.utils.ComposeEnumUtil;

import java.util.List;

public class Rename {

    public static void main(String[] args) {
        // 以太钱包地址为16进制组合
        String[] arr_0x16 = { "0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f" };
        List<String> result = ComposeEnumUtil.compose( 1 , arr_0x16 );
        result.forEach( r -> {
            // alter table ts01 rename to ts01_new
            // alter table addr_{} rename to eth_addr_{}
            System.out.println( "alter table addr_0x"+r+" rename to eth_addr_0x" + r + ";" );
        } );
    }

}
