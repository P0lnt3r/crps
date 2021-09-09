package zy.pointer.crps.commons.business.cryptotx.repository.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName( "ETH_BLOCK_SYNCPM_HISTORY" )
@Data
public class EthBlockSyncPM_History extends EthBlockSyncPM {

    /**
     * 历史增量数据的原始数据
     */
    @TableField( "ADD_TX_RAW" )
    private String addTxRaw;

    /**
     * 历史增量数据的获取处理状态
     */
    @TableField( "ADD_SYNC_STATE" )
    private String addSyncState;

}
