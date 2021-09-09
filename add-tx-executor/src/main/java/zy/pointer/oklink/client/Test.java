package zy.pointer.oklink.client;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Test {

    public static void main(String[] args) throws Exception {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url( "https://www.oklink.com/api/explorer/v1/eth/addresses/0xbebc44782c7db0a1a60cb6fe97d0b483032ff1c7" )
                .get()
                .addHeader("x-apiKey","2c0c89b1-c74d-4454-9f9d-f1539aa0d287")
                .build();
        Call call = client.newCall( request ) ;
        Response response = call.execute();
        String responseString = response.body().string();
        System.out.println(responseString);
    }

}
