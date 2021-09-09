package zy.pointer.crps.commons.business.cryptotx.component;

import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;
import zy.pointer.crps.commons.business.cryptotx.repository.model.EthAddressTx;
import zy.pointer.crps.commons.business.cryptotx.repository.model.EthBlockTransaction;

public class EthBlockTransactionTableNameHandler implements TableNameHandler {

    @Override
    public String dynamicTableName(String sql, String tableName) {
        return tableName + getTableRange( EthBlockTransaction.BLOCK_HEIGHT.get() );
    }

    private String getTableRange( Long blockHeight ){
        if ( blockHeight <= 1000000 ){
            return "100";
        }else if ( blockHeight > 1000000 && blockHeight <= 2000000 ){
            return "200";
        }else if ( blockHeight > 2000000 && blockHeight <= 3000000 ){
            return "300";
        }else if ( blockHeight > 3000000 && blockHeight <= 4000000 ){
            return "400";
        } else if ( blockHeight > 4000000 && blockHeight <= 5000000 ){
            return "500";
        } else if ( blockHeight > 5000000 && blockHeight <= 6000000 ){
            return "600";
        } else if ( blockHeight > 6000000 && blockHeight <= 7000000 ){
            return "700";
        } else if ( blockHeight > 7000000 && blockHeight <= 8000000 ){
            return "800";
        } else if ( blockHeight > 8000000 && blockHeight <= 9000000 ){
            return "900";
        } else if ( blockHeight > 9000000 && blockHeight <= 10000000 ){
            return "1000";
        } else if ( blockHeight > 10000000 && blockHeight <= 11000000 ){
            return "1100";
        } else if ( blockHeight > 11000000 && blockHeight <= 12000000 ){
            return "1200";
        } else if ( blockHeight > 12000000 && blockHeight <= 13000000 ){
            return "1300";
        } else if ( blockHeight > 13000000 && blockHeight <= 14000000 ){
            return "1400";
        }
        return "1500";
    }

}
