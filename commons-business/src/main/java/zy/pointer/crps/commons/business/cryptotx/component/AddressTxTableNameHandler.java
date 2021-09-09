package zy.pointer.crps.commons.business.cryptotx.component;

import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;
import zy.pointer.crps.commons.business.cryptotx.repository.model.EthAddressTx;

/**
 * Mybatis - plus 分表插件实现
 * 针对 地址 的分表业务逻辑
 */
public class AddressTxTableNameHandler implements TableNameHandler {

    @Override
    public String dynamicTableName(String sql, String tableName) {
        String address = EthAddressTx.THREAD_LOCAL_ADDRESS.get();
        return tableName + address.substring( 0 , 5);
    }

}
