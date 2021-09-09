package cryptotx;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import test.SpringTestCase;
import zy.pointer.crps.commons.business.cryptotx.DictConstant;
import zy.pointer.crps.commons.business.cryptotx.repository.model.EthAddressTag;
import zy.pointer.crps.commons.business.cryptotx.repository.model.EthBlockSyncPM;
import zy.pointer.crps.commons.business.cryptotx.repository.model.TxMonitorStrategy;
import zy.pointer.crps.commons.business.cryptotx.service.IEthAddressTagService;
import zy.pointer.crps.commons.business.cryptotx.service.IEthAddressTxService;
import zy.pointer.crps.commons.business.cryptotx.service.IEthBlockSyncPMService;
import zy.pointer.crps.commons.business.cryptotx.service.ITxMonitorStrategyService;

import java.util.*;
import java.util.stream.Collectors;


public class BlockSyncPMServiceTest extends SpringTestCase {

    @Autowired
    IEthBlockSyncPMService blockSyncPMService;

    @Autowired
    IEthAddressTxService addressTxService;

    @Autowired
    ITxMonitorStrategyService txMonitorStrategyService;

    @Autowired
    IEthAddressTagService addressTagService;


    @Test
    public void testGet(){
        EthBlockSyncPM blockSyncPM = blockSyncPMService.getByBlockHeight( 10653325L );
        System.out.println( blockSyncPM.getTxRaw() );
    }

    @Test
    public void test(){
        List<EthBlockSyncPM> list = blockSyncPMService.getUnSync( 12573900L , 12573950L );
    }

    /**
     * 0x154bc456b521f5070eebdab14d1f36eab4d4c2c672a72115aca7f69adbcb2b01
     */
    @Test
    public void test3(){
        Set<String> addressTagCache = addressTagService.getAllAddressTag()
                .stream()
                .map( EthAddressTag::getAddress )
                .collect(Collectors.toSet());
        EthBlockSyncPM blockSyncPM = blockSyncPMService.getByBlockHeight( 12574000l );
        blockSyncPMService.saveBatchAddressTx( blockSyncPM , addressTagCache );
    }


    @Test
    public void test2(){
        EthBlockSyncPM blockSyncPM = blockSyncPMService.getByBlockHeight( 12573154L );
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
        blockSyncPMService.handleTxMonitorStrategy( blockSyncPM , map );
    }


}
