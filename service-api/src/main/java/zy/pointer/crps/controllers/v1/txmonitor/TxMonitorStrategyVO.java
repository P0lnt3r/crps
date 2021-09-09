package zy.pointer.crps.controllers.v1.txmonitor;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import zy.pointer.crps.commons.business.cryptotx.repository.model.TxMonitorStrategy;
import zy.pointer.crps.commons.framework.web.model.vo.AbsValueObject;
import zy.pointer.crps.commons.framework.web.model.vo.PageResponseVO;

@Data
@ApiModel( "监控策略" )
public class TxMonitorStrategyVO extends AbsValueObject<TxMonitorStrategy> {

    @ApiModelProperty( "监控名称" )
    private String title;

    @ApiModelProperty( "币种" )
    private String token;

    @ApiModelProperty( "主网链" )
    private String mainNet;

    @ApiModelProperty( "监控地址" )
    private String address;

    @ApiModelProperty( "监控策略" )
    private String strategy;

    @ApiModelProperty( "创建时间" )
    private String createTime;

}
