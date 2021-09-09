package zy.pointer.sync.pmeth.sql;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;
import zy.pointer.crps.commons.utils.ComposeEnumUtil;

import java.io.FileWriter;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 基于以太坊的地址特征对地址数据进行分表
 */
public class AddressSQLGenerator {

    public static void main(String[] args) throws Exception {
        // 以太钱包地址为16进制组合
        String[] arr_0x16 = { "0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f" };
        // SQL文件输出目录
        String fileOutPath = "E:\\create_eth_addr.sql";
        String template = loadTemplate();
        List<String> result = ComposeEnumUtil.compose( 2 , arr_0x16 );
        StringBuilder sb = new StringBuilder();
        result.forEach( e -> {
            sb.append(
                template.replaceAll( "#xxx#" , e )
            );
            sb.append("\n");
        } );
        FileWriter writer = null;
        writer = new FileWriter(fileOutPath);
        writer.write(sb.toString());
        writer.close();
        System.out.println("FINISH!");
    }

    private static String loadTemplate() throws Exception {
        ClassPathResource classPathResource = new ClassPathResource("address_sql.tplt");
        InputStream inputStream = classPathResource.getInputStream();
        String s = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        inputStream.close();
        return s;
    }

}
