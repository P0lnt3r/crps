package zy.pointer.crps.commons.business.cryptotx.repository.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import zy.pointer.crps.commons.framework.repository.BaseEntity;

@Data
@TableName("ETH_LOCAL_FILE_TASK")
public class EthLocalFileTask extends BaseEntity {

    @TableField( "SOURCE" )
    private String source;

    @TableField( "FILE_NAME" )
    private String fileName;

    @TableField( "POSITION" )
    private Integer position;

    /**
     * 文件执行状态: 0:未加载,1:执行中,2:执行完毕
     */
    @TableField( "STATE" )
    private String state;

}
