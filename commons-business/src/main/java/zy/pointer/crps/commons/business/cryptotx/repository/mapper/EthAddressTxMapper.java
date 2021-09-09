package zy.pointer.crps.commons.business.cryptotx.repository.mapper;

import org.apache.ibatis.annotations.Param;
import zy.pointer.crps.commons.business.cryptotx.repository.model.EthAddressTx;
import zy.pointer.crps.commons.business.cryptotx.repository.model.extras.EthAddressTx_Statistics;
import zy.pointer.crps.commons.framework.repository.RepositoryMapper;

import java.util.List;
import java.util.Map;

public interface EthAddressTxMapper extends RepositoryMapper<EthAddressTx> {

    List<Map< String , Object >> selectMainTxTarget(
            @Param("address") String address ,
            @Param("token") String token ,
            @Param("flowType") String flowType
    );


    List<EthAddressTx_Statistics> selectMainTxTargetStatistics(
            @Param("address") String address ,
            @Param("token") String token ,
            @Param("flowType") String flowType,
            @Param("limit") Integer limit
    );

}
