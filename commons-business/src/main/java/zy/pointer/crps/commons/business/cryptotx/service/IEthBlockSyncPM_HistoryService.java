package zy.pointer.crps.commons.business.cryptotx.service;

import zy.pointer.crps.commons.business.cryptotx.repository.model.EthBlockSyncPM_History;
import zy.pointer.crps.commons.framework.business.BusinessService;

import java.util.List;
import java.util.Set;

public interface IEthBlockSyncPM_HistoryService extends BusinessService<EthBlockSyncPM_History> {

    /**
     * 获取未进行区块数据增量解析的区块记录数据
     * @return
     */
    List< EthBlockSyncPM_History > getUnParse();

    /**
     * 获取未进行区块数据增量写入的区块记录数据
     */
    List< EthBlockSyncPM_History > getUnSync();

    EthBlockSyncPM_History insertAddTx( String addTxRaw , EthBlockSyncPM_History ethBlockSyncPM_history );

    EthBlockSyncPM_History handleAddTxSync( EthBlockSyncPM_History  ethBlockSyncPM_history , Set<String> addressTagCache );

}
