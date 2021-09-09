package zy.pointer.crps.commons.business.cryptotx.repository.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import zy.pointer.crps.commons.framework.repository.BaseEntity;

/**
 * 链上交易监控策略
 */
@TableName( "TX_MONITOR_STRATEGY" )
@Data
public class TxMonitorStrategy extends BaseEntity {

    /**
     * 标题
     */
    @TableField( "TITLE" )
    private String title;

    /**
     * 币种
     */
    @TableField( "TOKEN" )
    private String token;

    /**
     * 监控地址
     */
    @TableField( "ADDRESS" )
    private String address;

    /**
     * 资金流动类型 [ 1:流入 , 0:流出 , 2:流入|流出 ]
     */
    @TableField( "FLOW_TYPE" )
    private String flowType;

    /**
     * 资金数量
     */
    @TableField( "AMOUNT" )
    private String amount;

    /**
     * 时效
     */
    @TableField( "DAYS" )
    private Integer days;

}
