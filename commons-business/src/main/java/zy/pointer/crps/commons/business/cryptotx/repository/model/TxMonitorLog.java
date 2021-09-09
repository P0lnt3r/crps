package zy.pointer.crps.commons.business.cryptotx.repository.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import zy.pointer.crps.commons.framework.repository.BaseEntity;

import java.time.LocalDateTime;

@Data
@TableName( "TX_MONITOR_LOG" )
public class TxMonitorLog extends BaseEntity {

    @TableField( "REF_STRATEGY_ID" )
    private Long refStrategyId;

    @TableField( "TOKEN" )
    private String token;

    @TableField( "ADDRESS" )
    private String address;

    @TableField( "FLOW_TYPE" )
    private String flowType;

    @TableField( "REF_ADDRESS" )
    private String refAddress;

    @TableField( "AMOUNT" )
    private String amount;

    @TableField( "TX_HASH" )
    private String txHash;

    @TableField( "TX_TIME" )
    private LocalDateTime txTime;

    @TableField( "BLOCK_HEIGHT" )
    private Long blockHeight;

    @TableField( "REPORT" )
    private String report;

}
