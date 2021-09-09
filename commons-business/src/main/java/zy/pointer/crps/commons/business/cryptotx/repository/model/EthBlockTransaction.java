package zy.pointer.crps.commons.business.cryptotx.repository.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import zy.pointer.crps.commons.framework.repository.BaseEntity;

import java.time.LocalDateTime;

@TableName("ETH_BLOCK_TRANSACTION_")
@Data
public class EthBlockTransaction extends BaseEntity {

    public static final String TABLE_NAME_PREFIX = "ETH_BLOCK_TRANSACTION_";

    public static final ThreadLocal< Long > BLOCK_HEIGHT = new ThreadLocal<>();

    @TableField( "BLOCK_HEIGHT" )
    private Long blockHeight;

    @TableField( "TX_TIME" )
    private LocalDateTime txTime;

    @TableField( "TX_HASH" )
    private String txHash;

    @TableField( "_FROM" )
    private String from;

    @TableField( "_TO" )
    private String to;

    @TableField( "VALUE" )
    private String value;

    @TableField( "SYNC_STATE" )
    private String syncState;

    public void setBlockHeight(Long blockHeight) {
        this.blockHeight = blockHeight;
        BLOCK_HEIGHT.set( blockHeight );
    }
}
