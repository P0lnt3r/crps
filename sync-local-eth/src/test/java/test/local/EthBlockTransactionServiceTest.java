package test.local;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import test.SpringTestCase;
import zy.pointer.crps.commons.business.cryptotx.repository.model.EthBlockTransaction;
import zy.pointer.crps.commons.business.cryptotx.service.IEthBlockTransactionService;

import java.time.LocalDateTime;

public class EthBlockTransactionServiceTest extends SpringTestCase {

    @Autowired
    IEthBlockTransactionService ethBlockTransactionService;

    @Test
    public void test(){
        EthBlockTransaction ethBlockTransaction = new EthBlockTransaction();
        ethBlockTransaction.setBlockHeight( 6001231L );
        ethBlockTransaction.setTxHash( "0x1d3d61e4faa722149ff9df276f9fd94fc858d16d0f7903c0bd48a368085aaecf" );
        ethBlockTransaction.setTxTime(LocalDateTime.now());
        ethBlockTransaction.setFrom("0x4eeab941ae37db55e03eb9587088afe3a25016b1");
        ethBlockTransaction.setTo("0x8d3d4ea1c81f74b36602ab769d12a3e3c2b3336f");
        ethBlockTransaction.setValue("9.81826642");
        ethBlockTransaction.setSyncState("0");
        ethBlockTransactionService.save( ethBlockTransaction );
    }

}
