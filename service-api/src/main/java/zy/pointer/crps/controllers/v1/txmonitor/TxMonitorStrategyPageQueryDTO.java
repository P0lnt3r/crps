package zy.pointer.crps.controllers.v1.txmonitor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import zy.pointer.crps.commons.business.cryptotx.repository.model.TxMonitorStrategy;
import zy.pointer.crps.commons.framework.web.model.dto.PageQueryDTO;

@Data
@ApiModel( "监控策略查询请求" )
public class TxMonitorStrategyPageQueryDTO extends PageQueryDTO<TxMonitorStrategy> {

    @ApiModelProperty( "监控地址" )
    private String address;

    @ApiModelProperty( "监控名称" )
    private String title;

}
