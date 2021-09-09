package zy.pointer.crps.commons.business.cryptotx;

import zy.pointer.crps.commons.framework.IDictConstant;

public enum DictConstant implements IDictConstant {

    CRYPTO_TOKEN_ETH( "eth" , "以太币" , "crypto.token.eth" ),
    CRYPTO_TOKEN_ERC20_USDT( "erc20-usdt" , "ERC20-USDT" , "crypto.token.erc20-usdt" ),
    CRYPTO_TOKEN_TRX( "trx" , "波场币" , "crypto.token.trx" ),
    CRYPTO_TOKEN_TRC20_USDT( "trc20-usdt" , "TRC20-USDT" , "crypto.token.trc20-usdt" ) ,

    CRYPTO_ADDRESS_TX_FLOWTYPE_IN( "1" , "资金流动标识:流入" , "crypto.address.tx.flowType.in" ),
    CRYPTO_ADDRESS_TX_FLOWTYPE_OUT( "0" , "资金流动标识:流出" , "crypto.address.tx.flowType.out" ),
    CRYPTO_ADDRESS_TX_FLOWTYPE_IN_OUT( "2" , "资金流动标识:流入|流出" , "crypto.address.tx.flowType.in.out"  ),

    CRYPTO_ADDRESS_TX_TRANSACTION_TYPE_UNKNOWN( "1" , "普通交易" , "crypto.address.tx.transaction.type.unknown" ),
    CRYPTO_ADDRESS_TX_TRANSACTION_TYPE_DEPOSIT( "2" , "转入交易所" , "crypto.address.tx.transaction.type.deposit" ),
    CRYPTO_ADDRESS_TX_TRANSACTION_TYPE_WITHDRAW( "3" , "提出交易所" , "crypto.address.tx.transaction.type.withdraw" ),
    CRYPTO_ADDRESS_TX_TRANSACTION_TYPE_INTERNAL( "4" , "交易所内部交易" , "crypto.address.tx.transaction.type.internal" ),


    CRYPTO_BLOCK_SYNCPM_SYNC_WAITING( "0" , "区块数据记录标识:未记录" , "crypto.block.syncpm.sync.waiting" ),
    CRYPTO_BLOCK_SYNCPM_SYNC_FINISH( "1" ,  "区块数据记录标识:已记录" , "crypto.block.syncpm.sync.finish"),
    CRYPTO_BLOCK_SYNCPM_MONITE_WAITING( "0" , "区块数据监控标识:未处理" , "crypto.block.syncpm.monite.waiting" ),
    CRYPTO_BLOCK_SYNCPM_MONITE_FINISH( "1" , "区块数据监控标识:已处理" , "crypto.block.syncpm.monite.finish" ),

    CRYPTO_ADDRESS_TAG_TYPE_EXCHANGE( "1" , "地址标记类型:交易所" , "crypto.address.tag.type.exchange" ),

    SYNC_LOCAL_FILE_TASK_STATE_UNEXECUTE( "0" , "文件同步状态:未执行", "sync.local.file.task.state.unexecute" ),
    SYNC_LOCAL_FILE_TASK_STATE_EXECUTING( "1" , "文件同步状态:执行中", "sync.local.file.task.state.executing" ),
    SYNC_LOCAL_FILE_TASK_STATE_FINISH( "2" , "文件同步状态:执行完成", "sync.local.file.task.state.finish" ),

    NULL( "", "", "" );

    private DictConstant( String value , String desc , String unique  ){
        this.value = value;
        this.desc = desc;
        this.unique = unique;
    }
    public String value ,unique , desc ;

    @Override
    public String value() {
        return value;
    }

    @Override
    public String unique() {
        return unique;
    }

    @Override
    public String desc() {
        return desc;
    }

}
