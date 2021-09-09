package zy.pointer.crps.commons.business.cryptotx.repository.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import zy.pointer.crps.commons.framework.repository.BaseEntity;

import java.time.LocalDateTime;

@Data
@TableName("eth_addr_")
public class EthAddressTx extends BaseEntity {

    public static final String TABLE_NAME_PREFIX = "eth_addr_";

    public static final ThreadLocal<String> THREAD_LOCAL_ADDRESS = new ThreadLocal<>();

    /**
     * 钱包地址
     */
    @TableField("ADDRESS")
    private String address;

    @TableField( "ADDRESS_PROJECT" )
    private String addressProject;

    @TableField("ADDRESS_EXCHANGE_TYPE")
    private String addressExchangeType;

    @TableField("ADDRESS_TAG")
    private String addressTag;

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
     * 关联地址类型
     */
    @TableField("REF_ADDRESS_PROJECT")
    private String refAddressProject;

    /**
     * 关联地址交易所账户类型 [ User:充值地址 , Deposit:提现地址 ]
     */
    @TableField("REF_ADDRESS_EXCHANGE_TYPE")
    private String refAddressExchangeType;

    /**
     * 关联地址标签
     */
    @TableField("REF_ADDRESS_TAG")
    private String refAddressTag;

    /**
     * 代币简称 [ btc , eth , erc20-usdt , trc20-usdt ]
     */
    @TableField("TOKEN")
    private String token;

    /**
     * 转账金额
     */
    @TableField("AMOUNT")
    private String amount;

    /**
     * 交易哈希
     */
    @TableField("TX_HASH")
    private String txHash;

    /**
     * 入块高度
     */
    @TableField("BLOCK_HEIGHT")
    private Long blockHeight;

    /**
     * 交易时间
     */
    @TableField("TX_TIME")
    private LocalDateTime txTime;

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
