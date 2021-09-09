package zy.pointer.crps.controllers.v1.addresstag;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import zy.pointer.crps.commons.business.cryptotx.repository.model.EthAddressTag;
import zy.pointer.crps.commons.framework.web.model.vo.AbsValueObject;

@Data
public class EthAddressTagVO extends AbsValueObject<EthAddressTag> {

    @ApiModelProperty( "钱包地址" )
    private String address;

    @ApiModelProperty( "地址标签" )
    private String tag;

    @ApiModelProperty( "地址类型" )
    private String project;

    @ApiModelProperty( "交易所内部地址类型" )
    private String exchangeType;

}
