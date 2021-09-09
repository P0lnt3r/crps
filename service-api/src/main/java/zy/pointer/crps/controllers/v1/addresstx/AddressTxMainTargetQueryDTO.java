package zy.pointer.crps.controllers.v1.addresstx;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel( "获取指定地址交易对手统计数据" )
public class AddressTxMainTargetQueryDTO {

    @ApiModelProperty( "钱包地址" )
    private String address;

    @ApiModelProperty( "转账类型" )
    private String flowType;

    @ApiModelProperty( "代币种类" )
    private String token;

}
