package zy.pointer.crps.commons.business.cryptotx.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import zy.pointer.crps.commons.business.cryptotx.repository.model.EthAddressTag;
import zy.pointer.crps.commons.business.cryptotx.repository.model.EthBlockSyncPM;
import zy.pointer.crps.commons.business.cryptotx.repository.model.TxMonitorLog;
import zy.pointer.crps.commons.business.cryptotx.repository.model.TxMonitorStrategy;
import zy.pointer.crps.commons.business.cryptotx.syncpm.SimpleTxModel;
import zy.pointer.crps.commons.framework.business.BusinessService;

import java.util.List;

public interface ITxMonitorStrategyService extends BusinessService<TxMonitorStrategy> {

    /**
     * 通过币种获取所有的监控策略
     * @param tokens
     * @return
     */
    List< TxMonitorStrategy > getTxMonitorStrategyList( String ... tokens );

    List<TxMonitorLog> monitorStrategyCheck(EthBlockSyncPM blockSyncPM , SimpleTxModel simpleTxModel , List< TxMonitorStrategy > txMonitorStrategyList );

    IPage<TxMonitorStrategy> getPageQuery(IPage page , String address , String title );

}
