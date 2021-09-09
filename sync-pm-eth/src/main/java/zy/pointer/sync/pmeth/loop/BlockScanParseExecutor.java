package zy.pointer.sync.pmeth.loop;

import cn.hutool.json.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import zy.pointer.crps.commons.business.cryptotx.DictConstant;
import zy.pointer.crps.commons.business.cryptotx.repository.model.EthBlockSyncPM;
import zy.pointer.crps.commons.business.cryptotx.service.IEthBlockSyncPMService;
import zy.pointer.crps.commons.business.cryptotx.syncpm.SimpleTxModel;
import zy.pointer.crps.commons.utils.math.BigDecimalUtil;
import zy.pointer.sync.pmeth.oklink.client.OKLinkClient;
import zy.pointer.sync.pmeth.oklink.model.blockinfo.BlockInfo;
import zy.pointer.sync.pmeth.oklink.model.transaction.Tag;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Component
@EnableAsync
public class BlockScanParseExecutor implements InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger( BlockScanParseExecutor.class );

    @Value("${eth.sync.height}")
    private Long configSyncHeight;

    @Value("${eth.sync.end-height}")
    private Long configEndHeight;

    @Value("${eth.sync.switch}")
    private boolean configSyncSwitch;

    @Value("${eth.contract.erc20-usdt}")
    private String erc20USDTContractAddress;

    // 当前待解析区块高度
    private Long currentBlockHeight;

    @Autowired
    private IEthBlockSyncPMService blockSyncPMService;

    @Autowired
    private OKLinkClient client;

    @Override
    public void afterPropertiesSet() throws Exception {
        if ( configSyncSwitch ){
            LOG.info("数据同步开关已配置开启.");
            LOG.info("配置 起始同步高度:{} -> 终止同步高度:{}" , configSyncHeight , configEndHeight );
            // 查询指定区块区间已同步的最新高度 , 计算出当前程序同步的区块范围.
            Long dbLatestBlockHeight = blockSyncPMService.getLatestBlockHeight( configSyncHeight , configEndHeight );
            if ( dbLatestBlockHeight == 0 ){
                // 如果数据库不存在指定范围内的同步数据 , 则从配置的指定高度开始同步
                currentBlockHeight = configSyncHeight;
            }else{
                // 如果数据库存在数据,则从数据库中已同步的数据中的下一个开始同步
                currentBlockHeight = dbLatestBlockHeight + 1;
            }
            LOG.info("该程序执行同步范围:{} -> {}" , currentBlockHeight , configEndHeight == 0L ? "+ ∞ " : configEndHeight);
        }else{
            LOG.info("数据同步开关未配置开启 , 如需要开启 , 修改对应环境配置文件 .yml -> eth.sync.switch = true");
        }
    }

    @Scheduled( cron = "0/5 * * * * ?" )
    public void loop() throws Exception{
        if ( !configSyncSwitch ){
            LOG.info("数据同步开关未配置开启 , 如需要开启 , 修改对应环境配置文件 .yml -> eth.sync.switch = true");
            return;
        }
        Long targetHeight = 0L;
        Long mainNetBlockHeight = 0L;
        if ( configEndHeight != 0 ){
            targetHeight = configEndHeight;
            mainNetBlockHeight = -1L;
        }else{
            mainNetBlockHeight = getBlockHeight();
            targetHeight = mainNetBlockHeight;
        }
        if ( currentBlockHeight <= targetHeight ){
            LOG.info( "[{}] ~ [{}] ({}) / [{}] | 执行本轮区块同步: {} ~ {}" ,
                    currentBlockHeight , targetHeight , getPercent( currentBlockHeight , targetHeight ) ,
                    mainNetBlockHeight ,
                    currentBlockHeight , targetHeight
            );
        }
        while ( currentBlockHeight <= targetHeight ){
            LOG.info( "[{}] ~ [{}] ({}) / [{}] | 获取区块信息 blockHeight : {}" , currentBlockHeight , targetHeight , getPercent( currentBlockHeight , targetHeight ) ,mainNetBlockHeight , currentBlockHeight );
            /*
             这里取消通过读取 block 信息来获取交易时间,因为获取的交易列表中存在区块的打包时间.
             如果区块中没有交易数据,这个区块有没有打包时间也不重要了,只需要知道高度即可.
             */
//            BlockInfo blockInfo = getBlock( currentBlockHeight );
            List<SimpleTxModel> simpleTxModelList = parseBlock( currentBlockHeight );
            LocalDateTime blockTime = null;
            if ( simpleTxModelList != null && !simpleTxModelList.isEmpty() ){
                blockTime = LocalDateTime.ofEpochSecond( simpleTxModelList.get(0).getTxTime() , 0, ZoneOffset.ofHours(8));
            }
            String txRawJSON = JSONUtil.toJsonStr( simpleTxModelList );
            EthBlockSyncPM blockSyncPM = new EthBlockSyncPM();
            blockSyncPM.setRaw("");
            blockSyncPM.setTxRaw( txRawJSON );
            blockSyncPM.setBlockHeight( currentBlockHeight );
            blockSyncPM.setSyncState(DictConstant.CRYPTO_BLOCK_SYNCPM_SYNC_WAITING.value);
            blockSyncPM.setMoniteState( DictConstant.CRYPTO_BLOCK_SYNCPM_MONITE_WAITING.value );
            blockSyncPM.setBlockTime(   blockTime );
            blockSyncPMService.save( blockSyncPM );
            // 添加数据到同步队列
            BlockDataSyncExecutor.queue.put( blockSyncPM );
            currentBlockHeight ++ ;
        }
        LOG.info( "[{}] ~ [{}] ({}) / [{}] | 完成本轮区块同步: {} ~ {}" , currentBlockHeight - 1 , targetHeight , getPercent( currentBlockHeight - 1 , targetHeight ), mainNetBlockHeight , currentBlockHeight - 1 , targetHeight );
    }

    private BlockInfo getBlock( Long blockHeight ) {
        do {
            try {
                return client.getEthBlockInfo(blockHeight);
            } catch (Exception e){
                LOG.error("获取最新区块高度异常 , MESSAGE = {}" , e.getMessage() );
                e.printStackTrace();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        }while( true );
    }

    private Long getBlockHeight() throws Exception {
        do {
            try {
                return client.getEthChainInfo().getBlock().getHeight();
            } catch (Exception e){
                Thread.sleep(1000);
                LOG.error("获取最新区块高度异常 , MESSAGE = {}" , e.getMessage() );
                e.printStackTrace();
            }
        }while( true );
    }

    private String getPercent( Long a , Long b ){
        return BigDecimalUtil.div( a.toString() , b.toString() )
                .multiply(new BigDecimal("100")).setScale(2, RoundingMode.DOWN).toPlainString() + "%";
    }

    private List<SimpleTxModel> parseBlock( Long blockHeight ) throws Exception {
        List< SimpleTxModel > list = new ArrayList<>();
        client.getTransactionNoRestrict( blockHeight ).stream().filter( transactionNoRestrict -> {
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
        client.getERC20Transaction( blockHeight ).stream().filter( erc20Transaction -> {
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


    public static void main(String[] args) throws Exception {
        Long blockHeight = 12573939L;
        OKLinkClient client = new OKLinkClient();
        List< SimpleTxModel > list = new ArrayList<>();
        client.getTransactionNoRestrict( blockHeight ).stream().filter( transactionNoRestrict -> {
            return transactionNoRestrict.getIsContractCall() != null && ! transactionNoRestrict.getIsContractCall();
        } ).forEach( transactionNoRestrict -> {
            SimpleTxModel simpleTxModel = new SimpleTxModel();
            simpleTxModel.setFrom( transactionNoRestrict.getFrom() );
            simpleTxModel.setTo( transactionNoRestrict.getTo() );
            simpleTxModel.setToken("eth");
            simpleTxModel.setTxHash( transactionNoRestrict.getHash() );
            simpleTxModel.setFromTag( JSONUtil.toJsonStr( transactionNoRestrict.getFromTag() ) );
            simpleTxModel.setToTag( JSONUtil.toJsonStr( transactionNoRestrict.getToTag() ) );
            simpleTxModel.setAmount( new BigDecimal(transactionNoRestrict.getValue()).toPlainString()  );
            list.add( simpleTxModel );
        } );
        client.getERC20Transaction( blockHeight ).stream().filter( erc20Transaction -> {
            return "0xdac17f958d2ee523a2206206994597c13d831ec7".equals( erc20Transaction.getTokenContractAddress() );
        } ).forEach( erc20Transaction -> {
            SimpleTxModel simpleTxModel = new SimpleTxModel();
            simpleTxModel.setFrom( erc20Transaction.getFrom() );
            simpleTxModel.setTo( erc20Transaction.getTo() );
            simpleTxModel.setToken("erc20-usdt");
            simpleTxModel.setTxHash( erc20Transaction.getTxhash() );
            simpleTxModel.setFromTag( JSONUtil.toJsonStr( erc20Transaction.getFromTag() ) );
            simpleTxModel.setToTag( JSONUtil.toJsonStr( erc20Transaction.getToTag() ) );
            simpleTxModel.setAmount( new BigDecimal(erc20Transaction.getValue()).toPlainString()  );
            list.add( simpleTxModel );
        } );
        list.forEach(System.out::println);
    }

}
