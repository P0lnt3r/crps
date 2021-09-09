package zy.pointer.crps.controllers.v1.addresstag;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import zy.pointer.crps.commons.business.cryptotx.repository.model.EthAddressTag;
import zy.pointer.crps.commons.framework.web.model.dto.PageQueryDTO;

@Data
public class AddressTagQueryDTO extends PageQueryDTO<EthAddressTag> {

    @ApiModelProperty( value = "查询关键字" , required = true )
    @Length( max = 128 , message = "{addresstag.query.key.notmatch}")
    private String key;

}
