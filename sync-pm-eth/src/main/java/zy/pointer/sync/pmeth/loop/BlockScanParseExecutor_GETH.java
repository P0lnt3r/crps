package zy.pointer.sync.pmeth.loop;

import cn.hutool.json.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import zy.pointer.crps.commons.business.cryptotx.DictConstant;
import zy.pointer.crps.commons.business.cryptotx.repository.model.EthBlockSyncPM;
import zy.pointer.crps.commons.business.cryptotx.service.IEthBlockSyncPMService;
import zy.pointer.crps.commons.business.cryptotx.syncpm.SimpleTxModel;
import zy.pointer.crps.commons.utils.math.BigDecimalUtil;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * GETH 版本 , 最后决定使用 OKLINK 来做
 */
//@Component
//@EnableAsync
public class BlockScanParseExecutor_GETH implements InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger( BlockScanParseExecutor_GETH.class );

    @Value("${eth.client.endpoint}")
    private String endpoint;

    @Value("${eth.sync.height}")
    private String configSyncHeight;

    @Value("${eth.sync.switch}")
    private String configSyncSwitch;

    @Value("${eth.contract.erc20-usdt}")
    private String erc20USDTContractAddress;

    private Admin admin;

    // DB已解析完成的区块高度
    private Long dbLatestBlockHeight;

    // 当前待解析区块高度
    private Long currentBlockHeight;

    @Autowired
    private IEthBlockSyncPMService blockSyncPMService;

    @Override
    public void afterPropertiesSet() throws Exception {
        if ( ! "true".equals( configSyncSwitch ) ){
            LOG.info("未配置数据扫描开关");
            return;
        }
        admin = Admin.build( new HttpService( endpoint ) );
        LOG.info(" 构建 ETH-RPC-CLIENT 成功");
        LOG.info("      - endpoint:{}" , endpoint);
        LOG.info("      - version:{}" , admin.web3ClientVersion().send().getWeb3ClientVersion());
        currentBlockHeight = getCurrentBlockHeight();
    }

    @Scheduled( cron = "0/5 * * * * ?" )
    public void loop(){
        if ( ! "true".equals( configSyncSwitch ) ){
            return;
        }
        Long blockHeight = getBlockHeight();
        while ( currentBlockHeight <= blockHeight ){
            EthBlock.Block block = getBlock( currentBlockHeight );
            LOG.info( "[{}/{}] 获取区块数据 , blockHeight:{}-txSize:{},blockHash:{}" ,
                    currentBlockHeight , blockHeight ,
                    currentBlockHeight , block.getTransactions().size() ,block.getHash()
            );
            List<SimpleTxModel> list = parseBlock(block);
            String rawJSON = JSONUtil.toJsonStr( block );
            String txRawJSON = JSONUtil.toJsonStr( list );
            LOG.info("[{}/{}] 解析结果:{}" ,  currentBlockHeight , blockHeight , txRawJSON);
            EthBlockSyncPM blockSyncPM = new EthBlockSyncPM();
            blockSyncPM.setRaw( rawJSON );
            blockSyncPM.setTxRaw( txRawJSON );
            blockSyncPM.setBlockHeight( currentBlockHeight );
            blockSyncPM.setSyncState(DictConstant.CRYPTO_BLOCK_SYNCPM_SYNC_WAITING.value);
            blockSyncPM.setMoniteState( DictConstant.CRYPTO_BLOCK_SYNCPM_MONITE_WAITING.value );
            blockSyncPM.setBlockTime( LocalDateTime.ofEpochSecond(block.getTimestamp().longValue(), 0, ZoneOffset.ofHours(8)) );
            blockSyncPMService.save( blockSyncPM );
            currentBlockHeight ++ ;
            if ( currentBlockHeight == blockHeight ){
                blockHeight = getBlockHeight();
            }
        }
        LOG.info( "已完成最新区块同步,DB:{}/NETWORK:{}" , currentBlockHeight - 1 , blockHeight );
    }

    private EthBlock.Block getBlock( Long blockHeight ){
        do{
            try {
                return  admin.ethGetBlockByNumber(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf( currentBlockHeight )) , true
                ).send().getBlock();
            } catch ( Exception e ) {
                LOG.error( "获取区块失败,ERROR:{}" , e.getMessage() );
                try {
                    Thread.sleep( 3000 );
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        }while( true );
    }

    private Long getBlockHeight(){
        do{
            try {
                return admin.ethBlockNumber().send().getBlockNumber().longValue();
            } catch ( Exception e ) {
                LOG.error( "获取区块高度失败,ERROR:{}" , e.getMessage() );
                try {
                    Thread.sleep( 3000 );
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        }while( true );
    }

    private Long getCurrentBlockHeight(){
        Long latestBlockHeight = blockSyncPMService.getLatestBlockHeight() ;
        Long configBlockHeight = Long.parseLong( configSyncHeight );
        Long height = latestBlockHeight > configBlockHeight ? latestBlockHeight + 1 : configBlockHeight;
        if ( height == 0 ){
            height = getBlockHeight();
        }
        return height;
    }

    private String getContractAddress( String txHash ){
        do{
            try {
                return admin.ethGetTransactionReceipt( txHash ).send().getTransactionReceipt().get().getContractAddress();
            } catch ( Exception e ) {
                LOG.error( "获取区块高度失败,ERROR:{}" , e.getMessage() );
                try {
                    Thread.sleep( 3000 );
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        }while( true );
    }

    private List<SimpleTxModel> parseBlock(EthBlock.Block block){
        List< SimpleTxModel > list = new ArrayList<>();
        block.getTransactions().forEach( transactionResult -> {
            EthBlock.TransactionObject transaction = (EthBlock.TransactionObject)transactionResult;
            String hash = transaction.getHash();
            String from = transaction.getFrom();
            String to   = transaction.getTo();
            String amount = Convert.fromWei( transaction.getValue().toString() , Convert.Unit.ETHER ).toPlainString();
            if ( to == null ){
                // 存在特殊交易结构 , 非 from -> to 结构的交易数据 , 大概率为 合约关联的交易
                to = getContractAddress( hash );
            }
            if ( erc20USDTContractAddress.equals( to ) ){
                try {
                    String input = transaction.getInput();
                    String cmd = input.substring(0,10);
                    if( "0xa9059cbb".equals(cmd) ){	// transfer
                        List<Type> values = FunctionReturnDecoder.decode(
                                input.substring(input.length()-64,input.length()),
                                org.web3j.abi.Utils.convert(Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}))
                        );
                        amount = values.get(0).getValue().toString();
                        to = input.substring(10,74);
                        List<Type> addressType = FunctionReturnDecoder.decode(
                                to,
                                org.web3j.abi.Utils.convert(Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}))
                        );
                        to = addressType.get(0).getValue().toString();
                    }else if("0x23b872dd".equals(cmd)){	//transferForm
                        List<Type> values = FunctionReturnDecoder.decode(
                                input.substring(input.length()-64,input.length()),
                                org.web3j.abi.Utils.convert(Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}))
                        );
                        amount = values.get(0).getValue().toString();
                        to = input.substring(74,138);
                        List<Type> addressType = FunctionReturnDecoder.decode(
                                to,
                                org.web3j.abi.Utils.convert(Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}))
                        );
                        to = addressType.get(0).getValue().toString();
                    }
                    amount = BigDecimalUtil.strDiv( amount , "1000000" , 8 );
                    list.add( new SimpleTxModel( hash , from , to , "erc20-usdt" , amount ) );
                }catch (Exception e){
                    // INPUT 数据异常,链上不会执行,这条数据作废
                    return;
                }
            }else{
                if (BigDecimal.ZERO.compareTo( new BigDecimal( amount ) ) != 0 ){
                    list.add( new SimpleTxModel( hash , from , to , "eth" , amount ) );
                }
            }
        } );
        return list;
    }


}
