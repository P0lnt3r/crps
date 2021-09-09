package zy.pointer.crps.controllers.v1.txmonitor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import zy.pointer.crps.commons.business.cryptotx.repository.model.TxMonitorLog;
import zy.pointer.crps.commons.framework.web.model.dto.PageQueryDTO;

@ApiModel( "监控日志分页查询" )
@Data
public class TxMonitorLogPageQueryDTO extends PageQueryDTO<TxMonitorLog> {

    @ApiModelProperty( "监控策略ID" )
    private Long refStrategyId;

}
