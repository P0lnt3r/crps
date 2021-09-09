package zy.pointer.crps.commons.business.cryptotx.repository.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import zy.pointer.crps.commons.framework.repository.BaseEntity;

/**
 * 地址标记
 */
@TableName("ETH_ADDRESS_TAG")
@Data
public class EthAddressTag extends BaseEntity {

    @TableField( "ADDRESS" )
    private String address;

    @TableField( "TAG" )
    private String tag;

    @TableField( "PROJECT" )
    private String project;

    /**
     * 交易所内地址类型
     */
    @TableField( "EXCHANGE_TYPE" )
    private String exchangeType;

}
