package cryptotx;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import test.SpringTestCase;
import zy.pointer.crps.commons.business.cryptotx.repository.model.AddressTx;
import zy.pointer.crps.commons.business.cryptotx.service.IAddressTxService;

public class AddressTxServiceTest extends SpringTestCase {

    @Autowired
    IAddressTxService addressTxService;

    @Test
    public void test(){
        AddressTx addressTx = new AddressTx();
        addressTx.setAddress("0x7390c3534d63e3153f238655be4bd8efab9441ed");
//        addressTxService.save( addressTx );
        int n = addressTxService.count();
        System.out.println(n);
    }

}
