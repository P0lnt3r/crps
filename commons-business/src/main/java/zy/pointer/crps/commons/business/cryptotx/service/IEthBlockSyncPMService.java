package zy.pointer.crps.commons.business.cryptotx.service;

import zy.pointer.crps.commons.business.cryptotx.repository.model.EthBlockSyncPM;
import zy.pointer.crps.commons.business.cryptotx.repository.model.TxMonitorStrategy;
import zy.pointer.crps.commons.framework.business.BusinessService;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IEthBlockSyncPMService extends BusinessService<EthBlockSyncPM> {

    /**
     * 获取最新已同步的区块高度
     * @return
     */
    Long getLatestBlockHeight();

    /**
     * 获取指定高度区间内已同步的最新高度.
     * @param startHeight
     * @param endHeight
     * @return
     */
    Long getLatestBlockHeight( Long startHeight , Long endHeight );

    /**
     * 获取未进行数据分表写入的区块数据
     * @return
     */
    List<EthBlockSyncPM> getUnSync();

    /**
     * 获取范围内未进行数据分表写入的区块数据
     * @param startHeight
     * @param endHeight
     * @return
     */
    List<EthBlockSyncPM> getUnSync(Long startHeight , Long endHeight );

    /**
     * 获取未进行数据监控检查的区块数据
     * @return
     */
    List<EthBlockSyncPM> getUnMonitor();

    /**
     * 通过区块高度获取对应的实体数据
     * @param blockHeight 区块高度
     * @return
     */
    EthBlockSyncPM getByBlockHeight(Long blockHeight );

    /**
     * 将一个区块中解析出的交易进行批量写入 AddressTx 分表中
     * @param blockSyncPM
     */
    void saveBatchAddressTx(EthBlockSyncPM blockSyncPM , Set<String> addressTagCache );

    /**
     * 对一个区块内的交易数据进行监控过滤检查
     * @param txMonitorStrategyList
     */
    void handleTxMonitorStrategy(EthBlockSyncPM blockSyncPM , Map<String , List<TxMonitorStrategy>> addressTxMonitorStrategyMap);

}
