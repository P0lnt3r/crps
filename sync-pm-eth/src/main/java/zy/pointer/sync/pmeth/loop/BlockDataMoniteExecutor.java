package zy.pointer.sync.pmeth.loop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import zy.pointer.crps.commons.business.cryptotx.DictConstant;
import zy.pointer.crps.commons.business.cryptotx.repository.model.EthBlockSyncPM;
import zy.pointer.crps.commons.business.cryptotx.repository.model.TxMonitorStrategy;
import zy.pointer.crps.commons.business.cryptotx.service.IEthBlockSyncPMService;
import zy.pointer.crps.commons.business.cryptotx.service.ITxMonitorStrategyService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 区块数据监控执行器
 */
@Component
@EnableAsync
public class BlockDataMoniteExecutor {

    private static final Logger LOG = LoggerFactory.getLogger( BlockDataMoniteExecutor.class );

    @Autowired
    ITxMonitorStrategyService txMonitorStrategyService;

    @Autowired
    IEthBlockSyncPMService blockSyncPMService;

    @Scheduled( cron = "0/5 * * * * ?" )
    public void loop(){
        List<TxMonitorStrategy> txMonitorStrategyList = txMonitorStrategyService.getTxMonitorStrategyList(
                DictConstant.CRYPTO_TOKEN_ETH.value , DictConstant.CRYPTO_TOKEN_ERC20_USDT.value
        );
        // Address (1):(N)  TxMonitorStrategyList 的数据结构
        Map< String , List<TxMonitorStrategy> > map = new HashMap<>();
        txMonitorStrategyList.forEach( txMonitorStrategy -> {
            String address = txMonitorStrategy.getAddress();
            List<TxMonitorStrategy> addressTxMonitorStrategyList = map.get( address );
            if ( addressTxMonitorStrategyList == null ){
                addressTxMonitorStrategyList = new ArrayList<>();
            }
            addressTxMonitorStrategyList.add( txMonitorStrategy );
            map.put( address , addressTxMonitorStrategyList );
        } );
        List<EthBlockSyncPM> blockSyncPMList = blockSyncPMService.getUnMonitor();
        blockSyncPMList.forEach( blockSyncPM -> {
            blockSyncPMService.handleTxMonitorStrategy( blockSyncPM , map );
        } );
    }


}
