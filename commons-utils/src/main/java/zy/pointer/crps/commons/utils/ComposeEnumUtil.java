package zy.pointer.crps.commons.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 字符组合工具
 */
public class ComposeEnumUtil {

    public static List<String> compose(Integer composeTime , String... arr ){
        List<String> result = new ArrayList<>();
        List<String> temp = new ArrayList<>();
        for( int i = 0;i< composeTime;i++ ){
            if ( i == 0 ){
                result.addAll( Arrays.asList( arr ) );
            }
            result.forEach( e -> temp.add( e ) );
            result.clear();
            temp.forEach( e-> {
                result.addAll( compose( e , arr ) );
            } );
            temp.clear();
        }
        return result;
    }

    public static List<String> compose(String in , String... arr ){
        List<String> result = new ArrayList<>();
        for (String e : arr) {
            result.add( in + e );
        }
        return result;
    }

}
