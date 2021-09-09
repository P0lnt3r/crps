package zy.pointer.crps.controllers.v1.txmonitor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import zy.pointer.crps.commons.business.cryptotx.repository.model.TxMonitorLog;
import zy.pointer.crps.commons.framework.web.model.vo.AbsValueObject;

@ApiModel( "监控日志" )
@Data
public class TxMonitorLogVo extends AbsValueObject<TxMonitorLog> {

    @ApiModelProperty( "交易时间" )
    private String txTime;

    @ApiModelProperty( "钱包地址" )
    private String address;

    @ApiModelProperty( "关联地址" )
    private String refAddress;

    @ApiModelProperty( "日志报告" )
    private String report;

}
