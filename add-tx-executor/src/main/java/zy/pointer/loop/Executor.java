package zy.pointer.loop;

import cn.hutool.json.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import zy.pointer.oklink.client.OKLinkClient;
import zy.pointer.oklink.model.transaction.Tag;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import zy.pointer.crps.commons.business.cryptotx.repository.model.EthBlockSyncPM_History;
import zy.pointer.crps.commons.business.cryptotx.service.IEthBlockSyncPM_HistoryService;
import zy.pointer.crps.commons.business.cryptotx.syncpm.SimpleTxModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

@Component
@EnableAsync
public class Executor implements InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger( Executor.class );

    @Value("${eth.contract.erc20-usdt}")
    String erc20USDTContractAddress;

    @Value("${oklink.x-apikeys}")
    String okLinkApiKeys;
    
    @Autowired
    IEthBlockSyncPM_HistoryService ethBlockSyncPM_historyService;

    private LinkedBlockingQueue< OKLinkClient > clientQueue = new LinkedBlockingQueue<>();

    private Set<String> addressTagCache = new HashSet<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        String[] keys = okLinkApiKeys.split(",");
        for (String key : keys) {
            OKLinkClient client = new OKLinkClient(key);
            clientQueue.put( client );
            LOG.info("构建 OKLINK-CLIENT:{}" , key);
        }
    }

    @Scheduled( cron = "0/5 * * * * ?" )
    public void parse() throws Exception {
        LOG.info("执行增量数据交易读取");
        List<EthBlockSyncPM_History> list = ethBlockSyncPM_historyService.getUnParse();
        list.parallelStream().forEach( ethBlockSyncPM_history -> {
            OKLinkClient client = null;
            try {
                client = getClient();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            Long blockHeight = ethBlockSyncPM_history.getBlockHeight();
            List<SimpleTxModel> simpleTxModelList = null;
            do{
                try {
                    simpleTxModelList = parseBlockForAdd( client ,  blockHeight);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }while(simpleTxModelList == null);
            try {
                clientQueue.put( client );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String addTxRawJSON = JSONUtil.toJsonStr( simpleTxModelList );
            ethBlockSyncPM_historyService.insertAddTx( addTxRawJSON , ethBlockSyncPM_history );
            LOG.info( "写入增量交易信息:{} , size = {}" , ethBlockSyncPM_history.getBlockHeight() , simpleTxModelList.size() );
        });
    }

    @Scheduled( cron = "0/5 * * * * ?" )
    public void sync(){
        LOG.info("执行增量数据交易写入");
        List<EthBlockSyncPM_History> list = ethBlockSyncPM_historyService.getUnSync();
        list.parallelStream().forEach( ethBlockSyncPM_history -> {
            ethBlockSyncPM_historyService.handleAddTxSync( ethBlockSyncPM_history , addressTagCache);
        } );
        if ( addressTagCache.size() > 100000 ){
            addressTagCache.clear();
        }
    }

    public OKLinkClient getClient() throws Exception {
        return clientQueue.take();
    }

    private List<SimpleTxModel> parseBlockForAdd( OKLinkClient client , Long blockHeight ) throws Exception {
        List< SimpleTxModel > list = new ArrayList<>();
        client.getAddTransactionNoRestrict( blockHeight ).stream().filter( transactionNoRestrict -> {
            return transactionNoRestrict.getIsContractCall() != null && ! transactionNoRestrict.getIsContractCall();
        } ).forEach( transactionNoRestrict -> {
            SimpleTxModel simpleTxModel = new SimpleTxModel();
            simpleTxModel.setFrom( transactionNoRestrict.getFrom() );
            simpleTxModel.setTo( transactionNoRestrict.getTo() );
            simpleTxModel.setToken("eth");
            simpleTxModel.setTxHash( transactionNoRestrict.getHash() );
            simpleTxModel.setFromTag( tagListToJson( transactionNoRestrict.getFromTag() ) );
            simpleTxModel.setToTag( tagListToJson( transactionNoRestrict.getToTag() ) );
            simpleTxModel.setAmount( new BigDecimal( transactionNoRestrict.getValue() ).toPlainString() );
            simpleTxModel.setTxTime( transactionNoRestrict.getBlocktime() );
            list.add( simpleTxModel );
        } );
        client.getAddERC20Transaction( blockHeight ).stream().filter( erc20Transaction -> {
            // 只过滤需要的 erc20 - usdt 合约内的交易
            return erc20USDTContractAddress.equals( erc20Transaction.getTokenContractAddress() );
        } ).forEach( erc20Transaction -> {
            SimpleTxModel simpleTxModel = new SimpleTxModel();
            simpleTxModel.setFrom( erc20Transaction.getFrom() );
            simpleTxModel.setTo( erc20Transaction.getTo() );
            simpleTxModel.setToken("erc20-usdt");
            simpleTxModel.setTxHash( erc20Transaction.getTxhash() );
            simpleTxModel.setFromTag( tagListToJson( erc20Transaction.getFromTag() ) );
            simpleTxModel.setToTag( tagListToJson( erc20Transaction.getToTag() ) );
            simpleTxModel.setAmount( new BigDecimal( erc20Transaction.getValue() ).toPlainString() );
            simpleTxModel.setTxTime( erc20Transaction.getBlocktime() );
            list.add( simpleTxModel );
        } );
        return list;
    }

    private String tagListToJson(List<Tag> tagList){
        return tagList != null && !tagList.isEmpty() ?
                JSONUtil.toJsonStr( JSONUtil.toJsonStr( tagList.get(0) ) ) : "{}";
    }

}
