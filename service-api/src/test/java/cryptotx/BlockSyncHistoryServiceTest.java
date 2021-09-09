package cryptotx;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import test.SpringTestCase;
import zy.pointer.crps.commons.business.cryptotx.repository.model.EthBlockSyncPM_History;
import zy.pointer.crps.commons.business.cryptotx.service.IEthBlockSyncPM_HistoryService;

import java.util.List;

public class BlockSyncHistoryServiceTest extends SpringTestCase {

    @Autowired
    IEthBlockSyncPM_HistoryService ethBlockSyncPM_historyService;



    @Test
    public void test(){
        List<EthBlockSyncPM_History> list = ethBlockSyncPM_historyService.getUnParse();
        list.forEach( ethBlockSyncPM_history -> {
            System.out.println( ethBlockSyncPM_history.getBlockHeight() + ":" + ethBlockSyncPM_history.getCreateTime() );
        });
    }




}
