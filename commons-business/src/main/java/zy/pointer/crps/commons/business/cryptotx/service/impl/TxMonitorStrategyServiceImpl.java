package zy.pointer.crps.commons.business.cryptotx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zy.pointer.crps.commons.business.cryptotx.DictConstant;
import zy.pointer.crps.commons.business.cryptotx.repository.mapper.TxMonitorStrategyMapper;
import zy.pointer.crps.commons.business.cryptotx.repository.model.EthAddressTag;
import zy.pointer.crps.commons.business.cryptotx.repository.model.EthBlockSyncPM;
import zy.pointer.crps.commons.business.cryptotx.repository.model.TxMonitorLog;
import zy.pointer.crps.commons.business.cryptotx.repository.model.TxMonitorStrategy;
import zy.pointer.crps.commons.business.cryptotx.service.ITxMonitorStrategyService;
import zy.pointer.crps.commons.business.cryptotx.syncpm.SimpleTxModel;
import zy.pointer.crps.commons.framework.business.AbsBusinessService;
import zy.pointer.crps.commons.utils.math.BigDecimalUtil;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@Primary
public class TxMonitorStrategyServiceImpl extends AbsBusinessService <TxMonitorStrategyMapper , TxMonitorStrategy> implements ITxMonitorStrategyService {

    private static final Logger LOG = LoggerFactory.getLogger( TxMonitorStrategyServiceImpl.class );

    @Override
    public List<TxMonitorStrategy> getTxMonitorStrategyList(String... tokens) {
        LambdaQueryWrapper< TxMonitorStrategy > queryWrapper = Wrappers.lambdaQuery();
        int count = 0;
        for (String token : tokens) {
            queryWrapper.eq( TxMonitorStrategy::getToken , token );
            if ( count != tokens.length - 1 ){
                queryWrapper.or();
            }
            count ++ ;
        }
        return getBaseMapper().selectList( queryWrapper );
    }

    @Override
    public List<TxMonitorLog> monitorStrategyCheck(EthBlockSyncPM blockSyncPM, SimpleTxModel simpleTxModel, List<TxMonitorStrategy> txMonitorStrategyList) {
        String from = simpleTxModel.getFrom();
        String to   = simpleTxModel.getTo();
        String amount = simpleTxModel.getAmount();
        String token = simpleTxModel.getToken();
        String txHash = simpleTxModel.getTxHash();
        List< TxMonitorLog > logList = new ArrayList<>();
        txMonitorStrategyList.stream().filter( txMonitorStrategy -> {
            return BigDecimalUtil.strcompareTo2( amount , txMonitorStrategy.getAmount() );
        } ).forEach( txMonitorStrategy -> {
            String flowType = txMonitorStrategy.getFlowType();
            String txFlowType = to.equals( txMonitorStrategy.getAddress() ) ? DictConstant.CRYPTO_ADDRESS_TX_FLOWTYPE_IN.value : DictConstant.CRYPTO_ADDRESS_TX_FLOWTYPE_OUT.value;
            if ( ! DictConstant.CRYPTO_ADDRESS_TX_FLOWTYPE_IN_OUT.value.equals( flowType )
                 && !flowType.equals(txFlowType) ){
                return;
            }
            TxMonitorLog log = new TxMonitorLog();
            log.setAddress( txMonitorStrategy.getAddress() );
            log.setRefAddress( DictConstant.CRYPTO_ADDRESS_TX_FLOWTYPE_IN.value.equals(txFlowType) ? from : to );
            log.setAmount( amount );
            log.setTxHash( txHash );
            log.setRefStrategyId( txMonitorStrategy.getId() );
            log.setFlowType( txFlowType );
            log.setBlockHeight( blockSyncPM.getBlockHeight() );
            log.setTxTime( blockSyncPM.getBlockTime() );
            log.setToken( token );
            logList.add( log );
            LOG.info("[监控触发] {} @ {} {} -> {} => ({}){}" , txMonitorStrategy.getAddress() ,  blockSyncPM.getBlockHeight() , from , to , token , amount);
        } );
        return logList;
    }

    @Override
    public IPage<TxMonitorStrategy> getPageQuery(IPage page, String address, String title ) {
        LambdaQueryWrapper< TxMonitorStrategy > queryWrapper = Wrappers.lambdaQuery();
        if ( address != null && !"".equals( address ) ){
            queryWrapper.eq( TxMonitorStrategy::getAddress , address );
        }else {
            queryWrapper.ne( TxMonitorStrategy::getAddress , "*" );
        }
        if ( title != null && !"".equals( title ) ){
            queryWrapper.eq(TxMonitorStrategy::getTitle , title );
        }
        return getBaseMapper().selectPage( page , queryWrapper );
    }
}
