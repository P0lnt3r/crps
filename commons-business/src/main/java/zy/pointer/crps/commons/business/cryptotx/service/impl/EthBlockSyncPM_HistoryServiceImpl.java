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
import zy.pointer.crps.commons.business.cryptotx.repository.mapper.EthBlockSyncPM_HistoryMapper;
import zy.pointer.crps.commons.business.cryptotx.repository.model.EthAddressTag;
import zy.pointer.crps.commons.business.cryptotx.repository.model.EthAddressTx;
import zy.pointer.crps.commons.business.cryptotx.repository.model.EthBlockSyncPM_History;
import zy.pointer.crps.commons.business.cryptotx.service.IEthAddressTagService;
import zy.pointer.crps.commons.business.cryptotx.service.IEthAddressTxService;
import zy.pointer.crps.commons.business.cryptotx.service.IEthBlockSyncPM_HistoryService;
import zy.pointer.crps.commons.business.cryptotx.syncpm.SimpleTxModel;
import zy.pointer.crps.commons.framework.business.AbsBusinessService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Primary
@Transactional
public class EthBlockSyncPM_HistoryServiceImpl extends AbsBusinessService<EthBlockSyncPM_HistoryMapper , EthBlockSyncPM_History>
                implements IEthBlockSyncPM_HistoryService {

    private static final Logger LOG = LoggerFactory.getLogger( EthBlockSyncPM_HistoryServiceImpl.class );

    @Autowired
    IEthAddressTxService ethAddressTxService;

    @Autowired
    IEthAddressTagService ethAddressTagService;

    @Override
    public List<EthBlockSyncPM_History> getUnParse() {
        LambdaQueryWrapper< EthBlockSyncPM_History > queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.isNull( EthBlockSyncPM_History::getAddSyncState );
        queryWrapper.orderByDesc( EthBlockSyncPM_History::getBlockHeight );
        queryWrapper.last( "limit 1000" );
        return getBaseMapper().selectList( queryWrapper );
    }

    @Override
    public List<EthBlockSyncPM_History> getUnSync() {
        LambdaQueryWrapper< EthBlockSyncPM_History > queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq( EthBlockSyncPM_History::getAddSyncState , DictConstant.CRYPTO_BLOCK_SYNCPM_SYNC_WAITING.value );
        queryWrapper.orderByDesc( EthBlockSyncPM_History::getBlockHeight );
        queryWrapper.last( "limit 1000" );
        return getBaseMapper().selectList( queryWrapper );
    }

    @Override
    public EthBlockSyncPM_History insertAddTx(String addTxRaw, EthBlockSyncPM_History ethBlockSyncPM_history) {
        ethBlockSyncPM_history.setAddTxRaw( addTxRaw );
        ethBlockSyncPM_history.setAddSyncState(DictConstant.CRYPTO_BLOCK_SYNCPM_SYNC_WAITING.value);
        getBaseMapper().updateById( ethBlockSyncPM_history );
        return ethBlockSyncPM_history;
    }

    @Override
    public EthBlockSyncPM_History handleAddTxSync(EthBlockSyncPM_History ethBlockSyncPM_history , Set<String> addressTagCache  ) {
        String txRawJSON = ethBlockSyncPM_history.getAddTxRaw();
        List<EthAddressTag> addressTagList = new ArrayList<>();
        List<SimpleTxModel> list = JSONUtil.parseArray( txRawJSON ).toList(SimpleTxModel.class);
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
            // 交易类型判断 : 1:普通交易 , 2:转入交易所 , 3:转出交易所 , 4:内部交易
            /*
                如果是普通交易,则意味着 project 都不是 Exchange
             */
            if ( ! "Exchange".equals( fromTag_Project ) && ! "Exchange".equals( toTag_Project ) ){
                transactionType = DictConstant.CRYPTO_ADDRESS_TX_TRANSACTION_TYPE_UNKNOWN.value;
            }
            if ( "Exchange".equals( fromTag_Project ) || "Exchange".equals( toTag_Project ) ){
                if (Objects.equals( fromTag_Tag , toTag_Tag ) ){
                    // 交易所内部交易.可能是内部系统的汇总操作,也可能是两个普通用户发起的链上内部转账.但基本可以确定是内部交易
                    transactionType = DictConstant.CRYPTO_ADDRESS_TX_TRANSACTION_TYPE_INTERNAL.value;
                }else{
                    // 如果是从未知地址 -> 交易所地址
                    if ( fromTag_Project == null && "Exchange".equals(toTag_Project) ){
                        if ( "User".equals( toTag_Type ) ){
                            // 转入交易所
                            transactionType = DictConstant.CRYPTO_ADDRESS_TX_TRANSACTION_TYPE_DEPOSIT.value;
                        }else if( "Deposit".equals( toTag_Type ) ){
                            // 往交易所的已知地址 Deposit 地址中转币,只有两种可能,一种是交易所内部的汇总工作,另一种可能是有哪个脑瘫往别人地址上瞎鸡巴充币
                            // 所以大概率 FROM 是同属当前交易所的普通用户地址
                            transactionType = DictConstant.CRYPTO_ADDRESS_TX_TRANSACTION_TYPE_INTERNAL.value;
                            fromTag_Project = "Exchange";
                            fromTag_Tag = toTag_Tag;
                            fromTag_Type = "User";
                        }
                    }
                    // 如果是从 交易所地址 -> 未知地址
                    if ( toTag_Project == null && "Exchange".equals(fromTag_Project) ){
                        if ( "Deposit".equals( fromTag_Type ) ){
                            // 转出交易所
                            transactionType = DictConstant.CRYPTO_ADDRESS_TX_TRANSACTION_TYPE_WITHDRAW.value;
                        }else if( "User".equals( fromTag_Type ) ){
                            // 从交易所的已知用户地址上转出,大概率应该是交易所内部行为,且为汇总工作.
                            transactionType = DictConstant.CRYPTO_ADDRESS_TX_TRANSACTION_TYPE_INTERNAL.value;
                            toTag_Project = "Exchange";
                            toTag_Tag = fromTag_Tag;
                            toTag_Type = "Deposit";
                        }
                    }
                    // 如果是从 交易所地址 -> 另一个交易所地址
                    // 如果是两个不同交易所发生转账行为 , 则预期 FROM = {Exchange}.Deposit , TO = {Exchange}.User
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
            addrTxFrom.setFlowType( DictConstant.CRYPTO_ADDRESS_TX_FLOWTYPE_OUT.value /* 流出 */ );
            addrTxFrom.setTxHash( txHash );
            addrTxFrom.setBlockHeight( ethBlockSyncPM_history.getBlockHeight() );
            addrTxFrom.setTxTime( ethBlockSyncPM_history.getBlockTime() );
            addrTxFrom.setAddressTag( getSafeTag(fromTag_Tag) );
            addrTxFrom.setAddressExchangeType( fromTag_Type );
            addrTxFrom.setAddressProject( fromTag_Project );
            addrTxFrom.setRefAddressExchangeType( toTag_Type );
            addrTxFrom.setRefAddressProject( toTag_Project );
            addrTxFrom.setRefAddressTag(getSafeTag( toTag_Tag ));
            addrTxFrom.setTransactionType( transactionType );
            ethAddressTxService.save( addrTxFrom );
            EthAddressTx addrTXTo = new EthAddressTx();
            addrTXTo.setAddress( to );
            addrTXTo.setRefAddress( from );
            addrTXTo.setAmount( amount );
            addrTXTo.setToken(token);
            addrTXTo.setFlowType( DictConstant.CRYPTO_ADDRESS_TX_FLOWTYPE_IN.value /* 流入 */ );
            addrTXTo.setTxHash( txHash );
            addrTXTo.setBlockHeight( ethBlockSyncPM_history.getBlockHeight() );
            addrTXTo.setTxTime( ethBlockSyncPM_history.getBlockTime() );
            addrTXTo.setAddressTag( getSafeTag( toTag_Tag ) );
            addrTXTo.setAddressExchangeType( toTag_Type );
            addrTXTo.setAddressProject( toTag_Project );
            addrTXTo.setRefAddressExchangeType( fromTag_Type );
            addrTXTo.setRefAddressProject( fromTag_Project );
            addrTXTo.setRefAddressTag(getSafeTag( fromTag_Tag ));
            addrTXTo.setTransactionType( transactionType );
            ethAddressTxService.save( addrTXTo );

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
        // 过滤当前区块筛选出的数据中可能存在的重复地址
        final List<EthAddressTag> noRepeatAddressTagList = new ArrayList<>();
        Set< String > noRepeatAddressSet = new HashSet<>();
        addressTagList.forEach( addressTag -> {
            if ( ! noRepeatAddressSet.contains( addressTag.getAddress() ) ){
                noRepeatAddressSet.add( addressTag.getAddress() );
                noRepeatAddressTagList.add( addressTag );
            }
        } );
        // 对比缓存数据再过滤一遍
        Set<String> newAddressSet = new HashSet<>();
        List<EthAddressTag> _noRepeatAddressTagList = noRepeatAddressTagList.stream().filter(addressTag -> {
            if ( ! addressTagCache.contains( addressTag.getAddress() ) ){
//                LOG.info("Saving ... Tag:{}.{}.{}.{}" , addressTag.getProject() , addressTag.getTag() , addressTag.getExchangeType() , addressTag.getAddress());
                newAddressSet.add(  addressTag.getAddress() );
                return true;
            }
            return false;
        } ).collect(Collectors.toList());
        ethAddressTagService.saveBatch( _noRepeatAddressTagList );
        ethBlockSyncPM_history.setAddSyncState(DictConstant.CRYPTO_BLOCK_SYNCPM_SYNC_FINISH.value);
        //数据更新到缓存
        addressTagCache.addAll( newAddressSet );
        LOG.info("[{}] 区块交易数据写入完成 , tx.size= {} , tags.size = {} " , ethBlockSyncPM_history.getBlockHeight() , list.size() , noRepeatAddressSet.size() );
        this.updateById( ethBlockSyncPM_history );
        return ethBlockSyncPM_history;
    }

    public String getSafeTag( String tag ){
        return   tag != null && tag.length() > 32 ?
                tag.substring( 0 , 30 ) : tag ;
    }


}
