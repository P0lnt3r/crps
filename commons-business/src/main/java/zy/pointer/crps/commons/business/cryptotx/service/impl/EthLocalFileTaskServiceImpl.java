package zy.pointer.crps.commons.business.cryptotx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zy.pointer.crps.commons.business.cryptotx.DictConstant;
import zy.pointer.crps.commons.business.cryptotx.repository.mapper.EthLocalFileTaskMapper;
import zy.pointer.crps.commons.business.cryptotx.repository.model.EthAddressTx;
import zy.pointer.crps.commons.business.cryptotx.repository.model.EthBlockTransaction;
import zy.pointer.crps.commons.business.cryptotx.repository.model.EthLocalFileTask;
import zy.pointer.crps.commons.business.cryptotx.service.IEthAddressTxService;
import zy.pointer.crps.commons.business.cryptotx.service.IEthBlockTransactionService;
import zy.pointer.crps.commons.business.cryptotx.service.IEthLocalFileTaskService;
import zy.pointer.crps.commons.framework.business.AbsBusinessService;
import zy.pointer.crps.commons.utils.math.BigDecimalUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Primary
@Service
@Transactional
@Slf4j
public class EthLocalFileTaskServiceImpl extends AbsBusinessService<EthLocalFileTaskMapper , EthLocalFileTask> implements IEthLocalFileTaskService {

    @Autowired
    IEthBlockTransactionService ethBlockTransactionService;

    @Autowired
    IEthAddressTxService ethAddressTxService;

    @Override
    public List<EthLocalFileTask> getUnExecuteFileTask(String source) {
        LambdaQueryWrapper< EthLocalFileTask > queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq( EthLocalFileTask::getSource , source );
        queryWrapper.ne( EthLocalFileTask::getState , DictConstant.SYNC_LOCAL_FILE_TASK_STATE_FINISH.value);
        queryWrapper.orderByAsc( EthLocalFileTask::getId );
        return getBaseMapper().selectList( queryWrapper );
    }

    @Override
    public void handleSyncLocalFile(EthLocalFileTask ethLocalFileTask, int position, String line) {
        // 0x1d2427d334277b98147239c591cd2b1763e8f7240c0b107b7313b0860d48a61f,3416508,1490452964,0.00042,0x52bc44d5378309ee2abf1539bf71de1b7d7be3b5,50000,20000000000,21000,0x46a0a5fb0492c53bd87532517d1ff2e9667a975adc0820844dbc7f2f76b3a59d,20,0,,0x1,0xf1c04d58b5f93d0c40c22e2f970110a3fbeff35b,0.20091231026214348,
        /*
           0:blockhash 0x1d2427d334277b98147239c591cd2b1763e8f7240c0b107b7313b0860d48a61f,
           1:blockheight 3416508,
           2:txtimestamp 1490452964,
           3:fee 0.00042,
           4:from 0x52bc44d5378309ee2abf1539bf71de1b7d7be3b5,
           5:gaslimit 50000,
           6:gasprice 20000000000,
           7:gascost 21000,
           8:txhash 0x46a0a5fb0492c53bd87532517d1ff2e9667a975adc0820844dbc7f2f76b3a59d,
           9:position 20,
           10:? 0,
           11:? ,
           12:status 0x1,
           13:to 0xf1c04d58b5f93d0c40c22e2f970110a3fbeff35b,
           14:value 0.20091231026214348,
         */

        String[] array = line.split(",");
        String from = array[ 4 ];
        String to   = array[ 13 ];
        String amount = array[ 14 ];
        amount = new BigDecimal( amount ).toPlainString();
        Long blockHeight = Long.parseLong( array[1] );
        LocalDateTime txTime = LocalDateTime.ofEpochSecond( Long.parseLong(array[2])  , 0, ZoneOffset.ofHours(8));
        String txHash = array[ 8 ];
        String token = "eth";

        EthBlockTransaction ethBlockTransaction = new EthBlockTransaction();
        ethBlockTransaction.setSyncState( "1" );
        ethBlockTransaction.setFrom( from );
        ethBlockTransaction.setTo( to );
        ethBlockTransaction.setValue( amount );
        ethBlockTransaction.setTxTime( txTime );
        ethBlockTransaction.setTxHash( txHash );
        ethBlockTransaction.setBlockHeight( blockHeight );
        if ( BigDecimalUtil.strequals( "0" , amount ) || to.trim().equals("") || blockHeight < 1 ){
            ethBlockTransaction.setSyncState( "0" );
            ethBlockTransactionService.save( ethBlockTransaction );
            ethLocalFileTask.setPosition( position );
            this.updateById( ethLocalFileTask );
            log.info("Ignore @ {} - {}" , ethLocalFileTask.getFileName() , position);
            return;
        }
        ethBlockTransactionService.save( ethBlockTransaction );

        EthAddressTx addrTxFrom = new EthAddressTx();
        addrTxFrom.setAddress( from );
        addrTxFrom.setRefAddress(to);
        addrTxFrom.setAmount( amount );
        addrTxFrom.setToken(token);
        addrTxFrom.setFlowType( DictConstant.CRYPTO_ADDRESS_TX_FLOWTYPE_OUT.value /* 流出 */ );
        addrTxFrom.setTxHash( txHash );
        addrTxFrom.setBlockHeight( blockHeight );
        addrTxFrom.setTxTime( txTime );
        ethAddressTxService.save( addrTxFrom );
        EthAddressTx addrTXTo = new EthAddressTx();

        addrTXTo.setAddress( to );
        addrTXTo.setRefAddress( from );
        addrTXTo.setAmount( amount );
        addrTXTo.setToken(token);
        addrTXTo.setFlowType( DictConstant.CRYPTO_ADDRESS_TX_FLOWTYPE_IN.value /* 流入 */ );
        addrTXTo.setTxHash( txHash );
        addrTXTo.setBlockHeight( blockHeight );
        addrTXTo.setTxTime( txTime );
        ethAddressTxService.save( addrTXTo );

        // UPDATE POSITION CURSOR
        ethLocalFileTask.setPosition( position );
        this.updateById( ethLocalFileTask );
        log.info("Saving @ {} - {}" , ethLocalFileTask.getFileName() , position);
    }

}
