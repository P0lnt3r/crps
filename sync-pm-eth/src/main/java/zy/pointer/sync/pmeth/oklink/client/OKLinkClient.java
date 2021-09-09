package zy.pointer.sync.pmeth.oklink.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import zy.pointer.sync.pmeth.oklink.model.OKLinkResponse;
import zy.pointer.sync.pmeth.oklink.model.blockinfo.BlockInfo;
import zy.pointer.sync.pmeth.oklink.model.chaininfo.ChainInfo;
import zy.pointer.sync.pmeth.oklink.model.transaction.TransactionWrap;
import zy.pointer.sync.pmeth.oklink.model.transaction.erc20.ERC20Transaction;
import zy.pointer.sync.pmeth.oklink.model.transaction.norestrict.TransactionNoRestrict;

import java.util.List;

@Component
@Slf4j
public class OKLinkClient {

    public static final ThreadLocal<Long> VISIT_INTERVAL = new ThreadLocal<Long>();

    private static final Integer DEFAULT_LIMIT = 100;

    @Value("${oklink.x-apikey}")
    private String xApiKey;

    private static final String BASE_URL = "https://www.oklink.com";

    private static final String GET_ETH_CHAIN_INFO = "/api/explorer/v1/eth/info";

    private static final String GET_ETH_TRANSACTION_NO_RESTRICT = "/api/explorer/v1/eth/transactionsNoRestrict";

    private static final String GET_ETH_TRANSACTION_ERC20 = "/api/explorer/v1/eth/transfers?tokenType=ERC20";

    private static final String GET_ETH_BLOCK_INFO = "/api/explorer/v1/eth/blocks";

    private static final Logger LOG = LoggerFactory.getLogger(OKLinkClient.class);

    private OkHttpClient _client;

    public OKLinkClient(){
        _client = new OkHttpClient();
    }

    private Request buildRequest(String url ){
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("x-apiKey","2c0c89b1-c74d-4454-9f9d-f1539aa0d287")
//                .addHeader("x-apiKey",xApiKey)
                .build();
        return request;
    }

    public static void main(String[] args) throws Exception {
        OKLinkClient client = new OKLinkClient();
        Long blockHeight = 12948576L;
        List<TransactionNoRestrict> list = client.getTransactionNoRestrict( blockHeight  );
        list.forEach(System.out::println);
        System.out.println( list.size() );
        List< ERC20Transaction > erc20TransactionList = client.getERC20Transaction( blockHeight );
        erc20TransactionList.forEach(System.out::println);
        System.out.println( erc20TransactionList.size() );
    }

    private <T> T getData( Call call , TypeReference<OKLinkResponse<T>> typeReference ) throws Exception {
        /**
         * OKLINK 对访问频率进行了限制 , 控制在 250MS 即可
         */
        Long now =  System.currentTimeMillis();
        Long lastVisitTime = VISIT_INTERVAL.get() == null ? 0L : VISIT_INTERVAL.get();
        if ( now - lastVisitTime < 250 ){
            Thread.sleep(250);
        }
        VISIT_INTERVAL.set(now);
        /**********************************************************/
        Response response = call.execute();
        String responseString = response.body().string();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OKLinkResponse<T> okResponse = mapper.readValue( responseString , typeReference);
        T data = okResponse.getData();
        if ( data == null ){
            LOG.error( responseString );
        }
        return data;
    }

    public BlockInfo getEthBlockInfo( Long blockHeight ) throws Exception {
        String url = BASE_URL + GET_ETH_BLOCK_INFO + "/" + blockHeight;
        LOG.info("访问 {} , 获取ETH - {} 区块信息" , url , blockHeight);
        Call call = _client.newCall( buildRequest( url ) ) ;
        return getData(call, new TypeReference<OKLinkResponse<BlockInfo>>() {});
    }

    public ChainInfo getEthChainInfo() throws Exception{
        LOG.info("访问 {} , 获取ETH链信息" , BASE_URL + GET_ETH_CHAIN_INFO);
        Call call = _client.newCall( buildRequest( BASE_URL + GET_ETH_CHAIN_INFO ) ) ;
        return getData(call, new TypeReference<OKLinkResponse<ChainInfo>>() {});
    }

    /**
     * 获取指定区块高度的 ERC20 交易数据 , 可通过返回的合约地址进行过滤获取 USDT 或者指定的其他 ERC20合约地址获取指定代币数据
     * @param blockHeight
     * @return
     * @throws Exception
     */
    public List<ERC20Transaction> getERC20Transaction( Long blockHeight ) throws Exception {
        String url = BASE_URL + GET_ETH_TRANSACTION_ERC20 + "&limit="+DEFAULT_LIMIT+"&blockHeight=" + blockHeight;
        Call call = _client.newCall( buildRequest( url ) ) ;
        TransactionWrap< ERC20Transaction > wrap = getData( call , new TypeReference<OKLinkResponse<TransactionWrap<ERC20Transaction>>>(){});
        int total = wrap.getTotal();
        LOG.info("访问 {} , 获取区块内ERC20交易 " , url );
        if ( total <= DEFAULT_LIMIT ){
            return wrap.getHits();
        }
        List< ERC20Transaction > result = wrap.getHits();
        int page = total / DEFAULT_LIMIT + 1;
        int currentPage = 2;
        while( currentPage <= page ){
            url = BASE_URL + GET_ETH_TRANSACTION_ERC20 + "&limit="+DEFAULT_LIMIT+"&blockHeight=" + blockHeight + "&offset=" + (currentPage - 1) * DEFAULT_LIMIT ;
            LOG.info("访问 {} , 获取区块内ERC20交易 " , url );
            call = _client.newCall( buildRequest( url ) ) ;
            wrap = getData( call , new TypeReference<OKLinkResponse<TransactionWrap<ERC20Transaction>>>(){});
            result.addAll( wrap.getHits() );
            currentPage ++ ;
        }
        return result;
    }

    /**
     * 获取第一页( limit = 100 )后的 ERC20 交易数据
     * @param blockHeight
     * @return
     * @throws Exception
     */
    public List<ERC20Transaction> getAddERC20Transaction( Long blockHeight ) throws Exception {
        String url = BASE_URL + GET_ETH_TRANSACTION_ERC20 + "&limit="+DEFAULT_LIMIT+"&blockHeight=" + blockHeight + "&offset=" + DEFAULT_LIMIT ;
        Call call = _client.newCall( buildRequest( url ) ) ;
        TransactionWrap< ERC20Transaction > wrap = getData( call , new TypeReference<OKLinkResponse<TransactionWrap<ERC20Transaction>>>(){});
        int total = wrap.getTotal();
        LOG.info("访问 {} , 获取区块内ERC20交易 " , url );
        int page = (total / DEFAULT_LIMIT) + 1;
        if ( page <= 2 ){
            return wrap.getHits();
        }
        int currentPage = 3;
        List< ERC20Transaction > result = wrap.getHits();
        while( currentPage <= page ){
            url = BASE_URL + GET_ETH_TRANSACTION_ERC20 + "&limit="+DEFAULT_LIMIT+"&blockHeight=" + blockHeight + "&offset=" + (currentPage - 1) * DEFAULT_LIMIT ;
            LOG.info("访问 {} , 获取区块内ERC20交易 " , url );
            call = _client.newCall( buildRequest( url ) ) ;
            wrap = getData( call , new TypeReference<OKLinkResponse<TransactionWrap<ERC20Transaction>>>(){});
            result.addAll( wrap.getHits() );
            currentPage ++ ;
        }
        return result;
    }


    /**
     * 获取指定区块高度的完整交易数据
     * @param blockHeight
     * @return
     * @throws Exception
     */
    public List<TransactionNoRestrict> getTransactionNoRestrict(Long blockHeight) throws Exception {
        String url = BASE_URL + GET_ETH_TRANSACTION_NO_RESTRICT + "?limit="+DEFAULT_LIMIT+"&blockHeight=" + blockHeight;
        Call call = _client.newCall( buildRequest( url ) ) ;
        TransactionWrap<TransactionNoRestrict> wrap = getData(call, new TypeReference<OKLinkResponse<TransactionWrap<TransactionNoRestrict>>>() {});
        int total = wrap.getTotal();
        LOG.info("访问 {},获取区块内交易信息 " , url );
        if ( total <= DEFAULT_LIMIT ){
            return wrap.getHits();
        }
        List<TransactionNoRestrict> result = wrap.getHits();
        int page = total / DEFAULT_LIMIT + 1;
        int currentPage = 2;
        while( currentPage <= page ){
            url = BASE_URL + GET_ETH_TRANSACTION_NO_RESTRICT + "?limit="+DEFAULT_LIMIT+"&blockHeight=" + blockHeight + "&offset=" + (currentPage - 1) * DEFAULT_LIMIT ;
            LOG.info("访问 {} , 获取区块内交易信息 " , url );
            call = _client.newCall( buildRequest( url ) ) ;
            wrap = getData(call, new TypeReference<OKLinkResponse<TransactionWrap<TransactionNoRestrict>>>() {});
            result.addAll( wrap.getHits() );
            currentPage ++;
        }
        return result;
    }

    /**
     * 获取第一页( limit = 100 )后的交易数据
     * @param blockHeight
     * @return
     * @throws Exception
     */
    public List<TransactionNoRestrict> getAddTransactionNoRestrict( Long blockHeight ) throws Exception{
        String url = BASE_URL + GET_ETH_TRANSACTION_NO_RESTRICT + "?limit="+DEFAULT_LIMIT+"&blockHeight=" + blockHeight + "&offset=" + DEFAULT_LIMIT;
        Call call = _client.newCall( buildRequest( url ) ) ;
        LOG.info("访问 {} , 获取区块内交易信息 " , url );
        TransactionWrap<TransactionNoRestrict> wrap = getData(call, new TypeReference<OKLinkResponse<TransactionWrap<TransactionNoRestrict>>>() {});
        int total = wrap.getTotal();
        int page = (total / DEFAULT_LIMIT) + 1;
        if ( page <= 2 ){
            return wrap.getHits();
        }
        int currentPage = 3;
        List<TransactionNoRestrict> result = wrap.getHits();
        while( currentPage <= page ){
            url = BASE_URL + GET_ETH_TRANSACTION_NO_RESTRICT + "?limit="+DEFAULT_LIMIT+"&blockHeight=" + blockHeight + "&offset=" + (currentPage-1) * DEFAULT_LIMIT ;
            LOG.info("访问 {} , 获取区块内交易信息 " , url );
            call = _client.newCall( buildRequest( url ) ) ;
            wrap = getData(call, new TypeReference<OKLinkResponse<TransactionWrap<TransactionNoRestrict>>>() {});
            result.addAll( wrap.getHits() );
            currentPage ++;
        }
        return result;
    }




}
