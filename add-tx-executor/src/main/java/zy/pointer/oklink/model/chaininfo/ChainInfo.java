package zy.pointer.oklink.model.chaininfo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import zy.pointer.oklink.model.OKLinkResponse;
import lombok.Data;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * https://www.oklink.com/docs/zh/#rest-api-common-chaininfo
 */
@Data
public class ChainInfo {

    private Block block;

    @Data
    public static class Block {
        private Long height;
    }

    public static void main(String[] args) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://www.oklink.com/api/explorer/v1/eth/info")
                .get()
                .addHeader("x-apiKey","2c0c89b1-c74d-4454-9f9d-f1539aa0d287")
                .build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            String responseString = response.body().string();
            System.out.println( responseString );
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            OKLinkResponse<ChainInfo> okResponse = mapper.readValue( responseString , new TypeReference< OKLinkResponse<ChainInfo> >(){});
            System.out.println( okResponse.getData().getBlock().height );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
