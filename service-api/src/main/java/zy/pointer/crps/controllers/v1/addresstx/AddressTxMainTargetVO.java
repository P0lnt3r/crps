package zy.pointer.crps.controllers.v1.addresstx;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import zy.pointer.crps.commons.business.cryptotx.repository.model.extras.EthAddressTx_Statistics;
import zy.pointer.crps.commons.framework.web.model.vo.AbsValueObject;

@ApiModel( "交易对手统计数据" )
@Data
public class AddressTxMainTargetVO extends AbsValueObject<EthAddressTx_Statistics> {

    private String address;

    private String addressProject;

    private String addressExchangeType;

    private String addressTag;

    private String refAddress;

    private String refAddressProject;

    private String refAddressExchangeType;

    private String refAddressTag;

    private String sumAmount;

    private String txCount;

    private String token;

    private String flowType;

}
