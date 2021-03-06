package zy.pointer.crps.commons.business.cryptotx.service.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zy.pointer.crps.commons.business.cryptotx.DictConstant;
import zy.pointer.crps.commons.business.cryptotx.repository.mapper.EthBlockSyncPMMapper;
import zy.pointer.crps.commons.business.cryptotx.repository.model.*;
import zy.pointer.crps.commons.business.cryptotx.service.*;
import zy.pointer.crps.commons.business.cryptotx.syncpm.SimpleTxModel;
import zy.pointer.crps.commons.framework.business.AbsBusinessService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Primary
@Transactional

public class EthBlockSyncPMServiceImpl extends AbsBusinessService<EthBlockSyncPMMapper, EthBlockSyncPM> implements IEthBlockSyncPMService {

    private static final Logger LOG = LoggerFactory.getLogger( EthBlockSyncPM.class );

    @Autowired
    IEthAddressTxService addressTxService;

    @Autowired
    ITxMonitorStrategyService txMonitorStrategyService;

    @Autowired
    ITxMonitorLogService txMonitorLogService;

    @Autowired
    IEthAddressTagService addressTagService;

    @Override
    public Long getLatestBlockHeight() {
        LambdaQueryWrapper<EthBlockSyncPM> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.orderByDesc( EthBlockSyncPM::getBlockHeight );
        queryWrapper.last( "limit 1" );
        List<EthBlockSyncPM> list = getBaseMapper().selectList( queryWrapper );
        if ( list == null || list.isEmpty() ){
            return 0L;
        }
        return list.get(0).getBlockHeight();
    }

    @Override
    public Long getLatestBlockHeight(Long startHeight, Long endHeight) {
        LambdaQueryWrapper<EthBlockSyncPM> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.ge( EthBlockSyncPM::getBlockHeight , startHeight );
        if ( endHeight != 0 ){
            queryWrapper.le( EthBlockSyncPM::getBlockHeight , endHeight );
        }
        queryWrapper.orderByDesc( EthBlockSyncPM::getBlockHeight );
        queryWrapper.last( "limit 1" );
        List<EthBlockSyncPM> list = getBaseMapper().selectList( queryWrapper );
        if ( list == null || list.isEmpty() ){
            return 0L;
        }
        return list.get(0).getBlockHeight();
    }

    @Override
    public List<EthBlockSyncPM> getUnSync() {
        LambdaQueryWrapper<EthBlockSyncPM> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.orderByAsc( EthBlockSyncPM::getBlockHeight );
        queryWrapper.eq( EthBlockSyncPM::getSyncState , DictConstant.CRYPTO_BLOCK_SYNCPM_SYNC_WAITING.value);
        return getBaseMapper().selectList( queryWrapper );
    }

    @Override
    public List<EthBlockSyncPM> getUnSync(Long startHeight, Long endHeight) {
        LambdaQueryWrapper<EthBlockSyncPM> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.ge( EthBlockSyncPM::getBlockHeight , startHeight );
        if ( endHeight != 0 ){
            queryWrapper.le( EthBlockSyncPM::getBlockHeight , endHeight );
        }
        queryWrapper.orderByAsc( EthBlockSyncPM::getBlockHeight );
        queryWrapper.eq( EthBlockSyncPM::getSyncState , DictConstant.CRYPTO_BLOCK_SYNCPM_SYNC_WAITING.value);
        queryWrapper.last( "limit 3000" );
        return getBaseMapper().selectList( queryWrapper );
    }

    @Override
    public List<EthBlockSyncPM> getUnMonitor() {
        LambdaQueryWrapper<EthBlockSyncPM> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.orderByAsc( EthBlockSyncPM::getId );
        queryWrapper.eq( EthBlockSyncPM::getMoniteState , DictConstant.CRYPTO_BLOCK_SYNCPM_MONITE_WAITING.value);
        return getBaseMapper().selectList( queryWrapper );
    }

    @Override
    public EthBlockSyncPM getByBlockHeight(Long blockHeight) {
        LambdaQueryWrapper<EthBlockSyncPM> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq( EthBlockSyncPM::getBlockHeight , blockHeight );
        return getBaseMapper().selectOne( queryWrapper );
    }

    @Override
    public void handleTxMonitorStrategy(EthBlockSyncPM blockSyncPM , Map<String , List<TxMonitorStrategy>> addressTxMonitorStrategyMap) {
        String txRawJSON = blockSyncPM.getTxRaw();
        List<TxMonitorLog> logList = new ArrayList<>();
        JSONUtil.parseArray( txRawJSON ).toList(SimpleTxModel.class).forEach( simpleTxModel -> {
            String from = simpleTxModel.getFrom();
            String to   = simpleTxModel.getTo();
            boolean fromHasTxMonitorStrategy = addressTxMonitorStrategyMap.containsKey( from );
            boolean toHasTxMonitorStrategy = addressTxMonitorStrategyMap.containsKey(to);
            if ( fromHasTxMonitorStrategy || toHasTxMonitorStrategy ){
                String address = fromHasTxMonitorStrategy ? from : to;
                logList.addAll(txMonitorStrategyService.monitorStrategyCheck( blockSyncPM , simpleTxModel , addressTxMonitorStrategyMap.get( address ) ));
            }
        });
        txMonitorLogService.saveBatch( logList );
        blockSyncPM.setMoniteState(DictConstant.CRYPTO_BLOCK_SYNCPM_MONITE_FINISH.value);
        this.updateById( blockSyncPM );
    }

    @Override
    public void saveBatchAddressTx(EthBlockSyncPM blockSyncPM , Set<String> addressTagCache ) {
        String txRawJSON = blockSyncPM.getTxRaw();
        List<EthAddressTag> addressTagList = new ArrayList<>();
        List< SimpleTxModel > list = JSONUtil.parseArray( txRawJSON ).toList(SimpleTxModel.class);
        list.forEach( simpleTxModel ->{
            /*
                {"item":"","project":"Exchange","logo":"","tag":"KuCoin","type":"User"}
             */
            String fromTagJson = simpleTxModel.getFromTag();
            JSONObject fromTag = JSONUtil.parseObj( fromTagJson );
            String fromTag_Project = fromTag.get("project" , String.class);
            String fromTag_Tag = fromTag.get("tag" , String.class);
            String fromTag_Type = fromTag.get("type" , String.class);
            String toTagJson = simpleTxModel.getToTag();
            JSONObject toTag = JSONUtil.parseObj( toTagJson );
            String toTag_Project = toTag.get("project" , String.class);
            String toTag_Tag = toTag.get("tag" , String.class);
            String toTag_Type = toTag.get("type" , String.class);
            String transactionType = DictConstant.CRYPTO_ADDRESS_TX_TRANSACTION_TYPE_UNKNOWN.value;
            // ?????????????????? : 1:???????????? , 2:??????????????? , 3:??????????????? , 4:????????????
            /*
                ?????????????????????,???????????? project ????????? Exchange
             */
            if ( ! "Exchange".equals( fromTag_Project ) && ! "Exchange".equals( toTag_Project ) ){
                transactionType = DictConstant.CRYPTO_ADDRESS_TX_TRANSACTION_TYPE_UNKNOWN.value;
            }
            if ( "Exchange".equals( fromTag_Project ) || "Exchange".equals( toTag_Project ) ){
                if (Objects.equals( fromTag_Tag , toTag_Tag ) ){
                    // ?????????????????????.????????????????????????????????????,?????????????????????????????????????????????????????????.????????????????????????????????????
                    transactionType = DictConstant.CRYPTO_ADDRESS_TX_TRANSACTION_TYPE_INTERNAL.value;
                }else{
                    // ???????????????????????? -> ???????????????
                    if ( fromTag_Project == null && "Exchange".equals(toTag_Project) ){
                        if ( "User".equals( toTag_Type ) ){
                            // ???????????????
                            transactionType = DictConstant.CRYPTO_ADDRESS_TX_TRANSACTION_TYPE_DEPOSIT.value;
                        }else if( "Deposit".equals( toTag_Type ) ){
                            // ??????????????????????????? Deposit ???????????????,??????????????????,???????????????????????????????????????,??????????????????????????????????????????????????????????????????
                            // ??????????????? FROM ?????????????????????????????????????????????
                            transactionType = DictConstant.CRYPTO_ADDRESS_TX_TRANSACTION_TYPE_INTERNAL.value;
                            fromTag_Project = "Exchange";
                            fromTag_Tag = toTag_Tag;
                            fromTag_Type = "User";
                        }
                    }
                    // ???????????? ??????????????? -> ????????????
                    if ( toTag_Project == null && "Exchange".equals(fromTag_Project) ){
                        if ( "Deposit".equals( fromTag_Type ) ){
                            // ???????????????
                            transactionType = DictConstant.CRYPTO_ADDRESS_TX_TRANSACTION_TYPE_WITHDRAW.value;
                        }else if( "User".equals( fromTag_Type ) ){
                            // ??????????????????????????????????????????,???????????????????????????????????????,??????????????????.
                            transactionType = DictConstant.CRYPTO_ADDRESS_TX_TRANSACTION_TYPE_INTERNAL.value;
                            toTag_Project = "Exchange";
                            toTag_Tag = fromTag_Tag;
                            toTag_Type = "Deposit";
                        }
                    }
                    // ???????????? ??????????????? -> ????????????????????????
                    // ???????????????????????????????????????????????? , ????????? FROM = {Exchange}.Deposit , TO = {Exchange}.User
                    if ( "Exchange".equals(fromTag_Project) && "Exchange".equals(toTag_Project) && !Objects.equals( fromTag_Tag , toTag_Tag ) ){
                        fromTag_Type = fromTag_Type == null ? "Deposit" : fromTag_Type;
                        toTag_Type = toTag_Type == null ? "User" : toTag_Type;
                        transactionType = DictConstant.CRYPTO_ADDRESS_TX_TRANSACTION_TYPE_WITHDRAW.value;
                    }
                }
            }
            String from = simpleTxModel.getFrom();
            String to   = simpleTxModel.getTo();
            String amount = simpleTxModel.getAmount();
            String token = simpleTxModel.getToken();
            String txHash = simpleTxModel.getTxHash();
            EthAddressTx addrTxFrom = new EthAddressTx();
            addrTxFrom.setAddress( from );
            addrTxFrom.setRefAddress(to);
            addrTxFrom.setAmount( amount );
            addrTxFrom.setToken(token);
            addrTxFrom.setFlowType( DictConstant.CRYPTO_ADDRESS_TX_FLOWTYPE_OUT.value /* ?????? */ );
            addrTxFrom.setTxHash( txHash );
            addrTxFrom.setBlockHeight( blockSyncPM.getBlockHeight() );
            addrTxFrom.setTxTime( blockSyncPM.getBlockTime() );
            addrTxFrom.setAddressTag( getSafeTag(fromTag_Tag) );
            addrTxFrom.setAddressExchangeType( fromTag_Type );
            addrTxFrom.setAddressProject( fromTag_Project );
            addrTxFrom.setRefAddressExchangeType( toTag_Type );
            addrTxFrom.setRefAddressProject( toTag_Project );
            addrTxFrom.setRefAddressTag(getSafeTag( toTag_Tag ));
            addrTxFrom.setTransactionType( transactionType );
            addressTxService.save( addrTxFrom );
            EthAddressTx addrTXTo = new EthAddressTx();
            addrTXTo.setAddress( to );
            addrTXTo.setRefAddress( from );
            addrTXTo.setAmount( amount );
            addrTXTo.setToken(token);
            addrTXTo.setFlowType( DictConstant.CRYPTO_ADDRESS_TX_FLOWTYPE_IN.value /* ?????? */ );
            addrTXTo.setTxHash( txHash );
            addrTXTo.setBlockHeight( blockSyncPM.getBlockHeight() );
            addrTXTo.setTxTime( blockSyncPM.getBlockTime() );
            addrTXTo.setAddressTag( getSafeTag( toTag_Tag ) );
            addrTXTo.setAddressExchangeType( toTag_Type );
            addrTXTo.setAddressProject( toTag_Project );
            addrTXTo.setRefAddressExchangeType( fromTag_Type );
            addrTXTo.setRefAddressProject( fromTag_Project );
            addrTXTo.setRefAddressTag(getSafeTag( fromTag_Tag ));
            addrTXTo.setTransactionType( transactionType );
            addressTxService.save( addrTXTo );

//            LOG.info("Saving ... [{}:{}] {}.{} => {}.{} = {} ({}) " , blockSyncPM.getBlockHeight(),txHash,
//                fromTag_Tag , from ,
//                toTag_Tag , to ,
//                amount , token
//            );

            // 2 Tags
            if ( fromTag_Project != null ){
                EthAddressTag fromAddressTag = new EthAddressTag();
                fromAddressTag.setAddress( from );
                fromAddressTag.setExchangeType( fromTag_Type );
                fromAddressTag.setTag( fromTag_Tag );
                fromAddressTag.setProject( fromTag_Project );
                addressTagList.add( fromAddressTag );
            }
            if ( toTag_Project != null ){
                EthAddressTag toAddressTag = new EthAddressTag();
                toAddressTag.setAddress( to );
                toAddressTag.setExchangeType( toTag_Type );
                toAddressTag.setTag( toTag_Tag );
                toAddressTag.setProject( toTag_Project );
                addressTagList.add( toAddressTag );
            }

        });
        // ??????????????????????????????????????????????????????????????????
        final List<EthAddressTag> noRepeatAddressTagList = new ArrayList<>();
        Set< String > noRepeatAddressSet = new HashSet<>();
        addressTagList.forEach( addressTag -> {
            if ( ! noRepeatAddressSet.contains( addressTag.getAddress() ) ){
                noRepeatAddressSet.add( addressTag.getAddress() );
                noRepeatAddressTagList.add( addressTag );
            }
        } );
        // ?????????????????????????????????
        Set<String> newAddressSet = new HashSet<>();
        List<EthAddressTag> _noRepeatAddressTagList = noRepeatAddressTagList.stream().filter(addressTag -> {
            if ( ! addressTagCache.contains( addressTag.getAddress() ) ){
//                LOG.info("Saving ... Tag:{}.{}.{}.{}" , addressTag.getProject() , addressTag.getTag() , addressTag.getExchangeType() , addressTag.getAddress());
                newAddressSet.add(  addressTag.getAddress() );
                return true;
            }
            return false;
        } ).collect(Collectors.toList());
        addressTagService.saveBatch( _noRepeatAddressTagList );
        blockSyncPM.setSyncState(DictConstant.CRYPTO_BLOCK_SYNCPM_SYNC_FINISH.value);
        //?????????????????????
        addressTagCache.addAll( newAddressSet );
        LOG.info("[{}] ?????????????????????????????? , tx.size= {} , tags.size = {} " , blockSyncPM.getBlockHeight() , list.size() , noRepeatAddressSet.size() );
        this.updateById( blockSyncPM );
    }

    public String getSafeTag( String tag ){
        return   tag != null && tag.length() > 32 ?
                tag.substring( 0 , 30 ) : tag ;
    }


}
