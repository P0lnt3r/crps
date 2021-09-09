package zy.pointer.crps.controllers.v1.addresstx;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zy.pointer.crps.commons.business.cryptotx.DictConstant;
import zy.pointer.crps.commons.business.cryptotx.repository.model.extras.EthAddressTx_Statistics;
import zy.pointer.crps.commons.business.cryptotx.service.IEthAddressTxService;
import zy.pointer.crps.commons.utils.math.BigDecimalUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Api( tags = "地址交易数据" )
@RequestMapping( "/api/v1/address" )
@RestController
public class AddressTxController {

    @Autowired
    IEthAddressTxService ethAddressTxService;

    @GetMapping( "/tx/getAddressTxMainTarget" )
    @ApiOperation( "获取指定地址的交易对手统计数据" )
    public List< AddressTxMainTargetVO > getAddressTxMainTarget(AddressTxMainTargetQueryDTO DTO){
        String address = DTO.getAddress();
        String flowType = DTO.getFlowType();
        String token = DTO.getToken();
        List< AddressTxMainTargetVO > data = new ArrayList<>();
        ethAddressTxService.getMainTxTargetStatistics(address,token,flowType,10).forEach( ethAddressTx_statistics -> {
            data.add(
                new AddressTxMainTargetVO().from( ethAddressTx_statistics , AddressTxMainTargetVO.class)
            );
        } );
        return data;
    }

    @GetMapping( "/tx/getAddressTxStatisticsDonut" )
    @ApiOperation("获取指定地址的交易轨迹数据")
    public AddressTxStatisticsDonutVO getAddressTxStatisticsDonut( String address , String token , Integer level ){
        List<EthAddressTx_Statistics> result = ethAddressTxService.getAddressTxTrajectory(address,token,level);
        AddressTxStatisticsDonutVO vo = new AddressTxStatisticsDonutVO();
        vo.setEdges( new ArrayList<AddressTxStatisticsDonutVO.Edge>());
        vo.setNodes( new ArrayList<AddressTxStatisticsDonutVO.Node>());
        result.stream().parallel().forEach( ethAddressTx_statistics -> {
            String id = ethAddressTx_statistics.getAddress();
            String label = ethAddressTx_statistics.getAddress();
            AddressTxStatisticsDonutVO.Node node = new AddressTxStatisticsDonutVO.Node();
            node.setId(id);
            node.setLabel( label );
            vo.addNode(node);
            AddressTxStatisticsDonutVO.Node node2 = new AddressTxStatisticsDonutVO.Node();
            node2.setId(ethAddressTx_statistics.getRefAddress());
            node2.setLabel( ethAddressTx_statistics.getRefAddress() );
            vo.addNode(node2);


            AddressTxStatisticsDonutVO.Edge edge = new AddressTxStatisticsDonutVO.Edge();
            edge.setSize(  Double.parseDouble( ethAddressTx_statistics.getSumAmount() ) );
            if (DictConstant.CRYPTO_ADDRESS_TX_FLOWTYPE_IN.value.equals( ethAddressTx_statistics.getFlowType() )){
                edge.setTarget( ethAddressTx_statistics.getAddress() );
                edge.setSource( ethAddressTx_statistics.getRefAddress() );
            }else{
                edge.setSource( ethAddressTx_statistics.getAddress() );
                edge.setTarget( ethAddressTx_statistics.getRefAddress() );
            }
            vo.getEdges().add( edge );
        } );
        return vo;
    }



}
