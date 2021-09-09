package zy.pointer.crps.commons.business.cryptotx.service;

import zy.pointer.crps.commons.business.cryptotx.repository.model.EthAddressTx;
import zy.pointer.crps.commons.business.cryptotx.repository.model.extras.EthAddressTx_Statistics;
import zy.pointer.crps.commons.framework.business.BusinessService;

import java.util.List;
import java.util.Map;

public interface IEthAddressTxService extends BusinessService<EthAddressTx> {

    List<EthAddressTx_Statistics> getMainTxTargetStatistics( String address,  String token , String flowType , Integer limit );

    List<EthAddressTx_Statistics> getMainTxTargetStatistics( String address , String token );

    List<EthAddressTx_Statistics> getAddressTxTrajectory(String address , String token, Integer level);

}
