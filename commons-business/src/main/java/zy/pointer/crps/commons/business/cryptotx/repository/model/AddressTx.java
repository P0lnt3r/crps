package zy.pointer.crps.commons.business.cryptotx.repository.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import zy.pointer.crps.commons.framework.repository.BaseEntity;

@Data
@TableName("addr_")
public class AddressTx extends BaseEntity {

    public static final String TABLE_NAME_PREFIX = "addr_";

    public static final ThreadLocal<String> THREAD_LOCAL_ADDRESS = new ThreadLocal<>();

    /**
     * 钱包地址
     */
    @TableField("ADDRESS")
    private String address;

    /**
     * 流入类型:[1:流入,0:流出]
     */
    @TableField("FLOW_TYPE")
    private String flowType;

    /**
     * 关联地址
     */
    @TableField("REF_ADDRESS")
    private String refAddress;

    /**
     * 代币简称 [ btc , eth , erc20-usdt , trc20-usdt ]
     */
    @TableField("TOKEN_TYPE")
    private String tokenType;

    /**
     * 转账金额
     */
    @TableField("AMOUNT")
    private String amount;

    /**
     * 交易哈希
     */
    @TableField("TX_ID")
    private String txId;

    /**
     * 入块高度
     */
    @TableField("BLOCK_HEIGHT")
    private Long blockHeight;

    /**
     * 交易类型
     */
    @TableField("TRANSACTION_TYPE")
    private String transactionType;

    public void setAddress( String address ){
        this.address = address;
        THREAD_LOCAL_ADDRESS.set( address );
    }

}
