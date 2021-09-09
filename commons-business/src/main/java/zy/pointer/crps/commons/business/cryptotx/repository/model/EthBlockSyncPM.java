package zy.pointer.crps.commons.business.cryptotx.repository.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import zy.pointer.crps.commons.framework.repository.BaseEntity;

import java.time.LocalDateTime;

/**
 * 区块 扫描|数据同步|监控检测|风控检测 状态记录
 */
@TableName( "ETH_BLOCK_SYNCPM" )
@Data
public class EthBlockSyncPM extends BaseEntity {

    /**
     * 区块高度
     */
    @TableField( "BLOCK_HEIGHT" )
    private Long blockHeight;

    /**
     * 区块生成时间
     */
    @TableField( "BLOCK_TIME" )
    private LocalDateTime blockTime;

    /**
     * 区块原始数据 : JSON
     */
    @TableField( "RAW" )
    private String raw;

    /**
     * 基于原始数据解析出的交易数据 : JSON
     */
    @TableField( "TX_RAW" )
    private String txRaw;

    /**
     * 同步交易写入分表记录的执行状态
     */
    @TableField( "SYNC_STATE" )
    private String syncState;

    /**
     * 交易监控检测状态
     */
    @TableField( "MONITE_STATE" )
    private String moniteState;

    /**
     * 防控检测状态
     */
    @TableField( "PREVENT_STATE" )
    private String preventState;

}
