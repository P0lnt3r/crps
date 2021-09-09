package zy.pointer.crps.commons.business.cryptotx.service.impl;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zy.pointer.crps.commons.business.cryptotx.DictConstant;
import zy.pointer.crps.commons.business.cryptotx.repository.mapper.EthAddressTxMapper;
import zy.pointer.crps.commons.business.cryptotx.repository.model.EthAddressTx;
import zy.pointer.crps.commons.business.cryptotx.repository.model.extras.EthAddressTx_Statistics;
import zy.pointer.crps.commons.business.cryptotx.service.IEthAddressTxService;
import zy.pointer.crps.commons.framework.business.AbsBusinessService;
import zy.pointer.crps.commons.utils.math.BigDecimalUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Primary
@Service
@Transactional
public class EthAddressTxServiceImpl extends AbsBusinessService<EthAddressTxMapper, EthAddressTx> implements IEthAddressTxService {

    @Transactional( readOnly = true )
    @Override
    public List<EthAddressTx_Statistics> getMainTxTargetStatistics(String address, String token, String flowType, Integer limit) {
        EthAddressTx.THREAD_LOCAL_ADDRESS.set( address );
        return getBaseMapper().selectMainTxTargetStatistics( address, token, flowType,limit );
    }

    @Transactional( readOnly = true )
    @Override
    public List<EthAddressTx_Statistics> getMainTxTargetStatistics(String address, String token) {
        List< EthAddressTx_Statistics > IN = getMainTxTargetStatistics( address , token , DictConstant.CRYPTO_ADDRESS_TX_FLOWTYPE_IN.value , null );
        List< EthAddressTx_Statistics > OUT = getMainTxTargetStatistics( address , token , DictConstant.CRYPTO_ADDRESS_TX_FLOWTYPE_OUT.value , null );
        List< EthAddressTx_Statistics > result = new ArrayList<>();
        Set< String > IN_REF_ADDRESS_SET = IN.stream().map( EthAddressTx_Statistics::getRefAddress ).collect(Collectors.toSet());
        result.addAll( IN );
        for (EthAddressTx_Statistics out : OUT) {
            if ( ! IN_REF_ADDRESS_SET.contains( out.getRefAddress() ) ){
                result.add( out );
                continue;
            }
            EthAddressTx_Statistics in = null;
            for (EthAddressTx_Statistics _in : IN) {
                if ( _in.getRefAddress().equals( out.getRefAddress() ) ){
                    in = _in;
                    break;
                }
            }
            String inAmount = in.getSumAmount();
            String outAmount = out.getSumAmount();
            if (BigDecimalUtil.strcompareTo2( inAmount , outAmount )){
                in.setSumAmount( BigDecimalUtil.strSub( inAmount , outAmount , 4 ) );
            }else{
                in.setFlowType( DictConstant.CRYPTO_ADDRESS_TX_FLOWTYPE_OUT.value );
                in.setSumAmount( BigDecimalUtil.strSub( outAmount , inAmount , 4 ) );
            }
        }
        return result;
    }

    @Override
    public List<EthAddressTx_Statistics> getAddressTxTrajectory(String address, String token, Integer level) {
        List< EthAddressTx_Statistics > data = getMainTxTargetStatistics( address , token );
        List<EthAddressTx_Statistics> IN_LIST = filter( data , DictConstant.CRYPTO_ADDRESS_TX_FLOWTYPE_IN.value );
        List<EthAddressTx_Statistics> OUT_LIST = filter( data , DictConstant.CRYPTO_ADDRESS_TX_FLOWTYPE_OUT.value );
        data.clear();
        for( int i = 1; i<=level;i++  ){
            List<EthAddressTx_Statistics> IN_LIST_RESULT = getAddressTxTrajectory( IN_LIST , token , DictConstant.CRYPTO_ADDRESS_TX_FLOWTYPE_IN.value );
            List<EthAddressTx_Statistics> OUT_LIST_RESULT = getAddressTxTrajectory( OUT_LIST , token , DictConstant.CRYPTO_ADDRESS_TX_FLOWTYPE_OUT.value );
            data.addAll( IN_LIST );
            data.addAll( OUT_LIST );
            IN_LIST.clear();
            IN_LIST.addAll( IN_LIST_RESULT );
            OUT_LIST.clear();
            OUT_LIST.addAll( OUT_LIST_RESULT );
        }
        return data;
    }

    private List< EthAddressTx_Statistics > getAddressTxTrajectory( List<EthAddressTx_Statistics> list , String token , String flowType){
        List<EthAddressTx_Statistics> data = new ArrayList<>();
        list.forEach( ethAddressTx_statistics -> {
            String address = ethAddressTx_statistics.getRefAddress();
            List< EthAddressTx_Statistics > _list = getMainTxTargetStatistics( address , token );
            _list = filter( _list , flowType );
            data.addAll( _list );
        } );
        return data;
    }

    private List< EthAddressTx_Statistics > filter( List< EthAddressTx_Statistics > list , String flowType ){
        return list.stream().filter( element -> {
            return element.getFlowType().equals( flowType );
        } ).collect(Collectors.toList());
    }


}
