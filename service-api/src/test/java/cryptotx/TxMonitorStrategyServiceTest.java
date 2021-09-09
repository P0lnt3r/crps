package cryptotx;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import test.SpringTestCase;
import zy.pointer.crps.commons.business.cryptotx.repository.model.TxMonitorStrategy;
import zy.pointer.crps.commons.business.cryptotx.service.ITxMonitorStrategyService;

import java.util.List;

public class TxMonitorStrategyServiceTest extends SpringTestCase {

    @Autowired
    ITxMonitorStrategyService txMonitorStrategyService;

    @Test
    public void test(){
        List<TxMonitorStrategy> list = txMonitorStrategyService.getTxMonitorStrategyList( "eth" , "erc20-usdt" );
        System.out.println( list );
    }

}
