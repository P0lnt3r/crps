package cryptotx;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import test.SpringTestCase;
import zy.pointer.crps.commons.business.cryptotx.repository.model.EthAddressTx;
import zy.pointer.crps.commons.business.cryptotx.repository.model.extras.EthAddressTx_Statistics;
import zy.pointer.crps.commons.business.cryptotx.service.IEthAddressTxService;

import java.util.List;
import java.util.Map;

public class AddressTxServiceTest extends SpringTestCase {

    @Autowired
    IEthAddressTxService addressTxService;

    @Test
    public void test(){

    }

    @Test
    public void testGetStatistics(){
        String address = "0x4b803e796e6a659445e7fb311cad3304324320bd";
        List<EthAddressTx_Statistics> result =  addressTxService.getMainTxTargetStatistics( address , "eth" , "1" , 5);
        ObjectMapper mapper = new ObjectMapper();
        result.forEach( s -> {
            System.out.println( JSONUtil.toJsonStr( s ) );
        } );
    }

    @Test
    public void testGetJ(){
        String address = "0x00dc01cbf44978a42e8de8e436edf94205cfb6ec";
        List<EthAddressTx_Statistics> result =  addressTxService.getAddressTxTrajectory(address , "eth" , 3 );
        result.forEach( s -> {
            System.out.println( JSONUtil.toJsonStr( s ) );
        } );
    }

}
