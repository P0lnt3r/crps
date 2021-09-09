package zy.pointer.sync.pmeth.oklink.model.blockinfo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import zy.pointer.crps.commons.utils.DateUtil;
import zy.pointer.sync.pmeth.oklink.model.OKLinkResponse;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * https://www.oklink.com/docs/zh/#rest-api-blocks-block
 */
@Data
public class BlockInfo {

    /** 区块高度 */
    private Long height;

    /** 时间戳 */
    private Long blocktime;

    private LocalDateTime blockTime;

    public LocalDateTime getBlockTime() {
        blockTime = LocalDateTime.ofEpochSecond( this.blocktime , 0, ZoneOffset.ofHours(8));
        return blockTime;
    }

    public static void main(String[] args) throws Exception {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url( "https://www.oklink.com/api/explorer/v1/eth/blocks/123456 " )
                .get()
                .addHeader("x-apiKey","2c0c89b1-c74d-4454-9f9d-f1539aa0d287")
                .build();
        Call call = client.newCall( request ) ;
        Response response = call.execute();
        String responseString = response.body().string();
        System.out.println(responseString);
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OKLinkResponse<BlockInfo> okResponse = mapper.readValue( responseString , new TypeReference<OKLinkResponse<BlockInfo>>(){} );
        System.out.println( DateUtil.getDateString( okResponse.getData().getBlockTime() ) );
    }

}
