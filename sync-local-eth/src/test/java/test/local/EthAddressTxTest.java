package test.local;

import test.SpringTestCase;
import zy.pointer.crps.commons.business.cryptotx.DictConstant;
import zy.pointer.crps.commons.business.cryptotx.repository.model.EthAddressTx;
import zy.pointer.crps.commons.business.cryptotx.repository.model.EthBlockTransaction;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class EthAddressTxTest {

    public static void main(String[] args) {

        String line = "0xd4e56740f876aef8c010b86a40d5f56745a118d0906a34e69aec8c0db1cb8fa3,0,1435634773,0.0,0xgenesis000000000000000000000000000000000,0,0,0,GENESIS_8251358ca4e060ddb559ca58bc0bddbeb4070203,8123,0,,0x1,0x8251358ca4e060ddb559ca58bc0bddbeb4070203,2000.0";
        String[] array = line.split(",");
        String from = array[ 4 ];
        String to   = array[ 13 ];
        String amount = array[ 14 ];
        Long blockHeight = Long.parseLong( array[1] );
        LocalDateTime txTime = LocalDateTime.ofEpochSecond( Long.parseLong(array[2])  , 0, ZoneOffset.ofHours(8));
        String txHash = array[ 8 ];
        String token = "eth";

        EthBlockTransaction ethBlockTransaction = new EthBlockTransaction();
        ethBlockTransaction.setSyncState( "1" );
        ethBlockTransaction.setFrom( from );
        ethBlockTransaction.setTo( to );
        ethBlockTransaction.setValue( amount );
        ethBlockTransaction.setTxTime( txTime );
        ethBlockTransaction.setTxHash( txHash );
        ethBlockTransaction.setBlockHeight( blockHeight );
        System.out.println( ethBlockTransaction );

        EthAddressTx addrTxFrom = new EthAddressTx();
        addrTxFrom.setAddress( from );
        addrTxFrom.setRefAddress(to);
        addrTxFrom.setAmount( amount );
        addrTxFrom.setToken(token);
        addrTxFrom.setFlowType( DictConstant.CRYPTO_ADDRESS_TX_FLOWTYPE_OUT.value /* 流出 */ );
        addrTxFrom.setTxHash( txHash );
        addrTxFrom.setBlockHeight( blockHeight );
        addrTxFrom.setTxTime( txTime );
        System.out.println( addrTxFrom );

        EthAddressTx addrTXTo = new EthAddressTx();
        addrTXTo.setAddress( to );
        addrTXTo.setRefAddress( from );
        addrTXTo.setAmount( amount );
        addrTXTo.setToken(token);
        addrTXTo.setFlowType( DictConstant.CRYPTO_ADDRESS_TX_FLOWTYPE_IN.value /* 流入 */ );
        addrTXTo.setTxHash( txHash );
        addrTXTo.setBlockHeight( blockHeight );
        addrTXTo.setTxTime( txTime );
        System.out.println( addrTXTo );

    }

}
