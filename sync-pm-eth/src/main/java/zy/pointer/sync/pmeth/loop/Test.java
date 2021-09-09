package zy.pointer.sync.pmeth.loop;

import zy.pointer.crps.commons.utils.math.BigDecimalUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Test {

    public static void main(String[] args) {
        System.out.println(  BigDecimalUtil.div( "12574001" , "12627349" ).multiply(new BigDecimal("100")).setScale(2, RoundingMode.DOWN).toPlainString() );
    }


}
