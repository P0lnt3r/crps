package zy.pointer.crps.commons.framework.repository;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 顶级实体类属性定义
 */
@Data
public class BaseEntity implements Serializable {

    /**
     * 物理主键
     */
    @TableId( value = "_ID" , type = IdType.AUTO )
    @ApiModelProperty("物理主键")
    private Long id;

    /**
     * 数据创建时间
     */
    @TableField( value = "_CREATE_TIME" , fill = FieldFill.INSERT )
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    @ApiModelProperty("数据创建时间")
    private LocalDateTime createTime;

    /**
     * 数据更新时间
     */
    @TableField( value = "_UPDATE_TIME" , fill = FieldFill.INSERT_UPDATE )
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    @ApiModelProperty("数据更新时间")
    private LocalDateTime updateTime;

    /**
     * 数据更新 Trace
     */
    @TableField( value = "_UPDATE_TRACE" )
    @JsonIgnore
    @ApiModelProperty("数据更新Trace")
    private String updateTrace;

    /**
     * 数据DB状态
     */
    @TableField( value = "_DB_STATE",fill = FieldFill.INSERT )
    @TableLogic
    @JsonIgnore
    @ApiModelProperty("数据保存状态,1:有效,0:逻辑删除")
    private Integer dbState;

}
