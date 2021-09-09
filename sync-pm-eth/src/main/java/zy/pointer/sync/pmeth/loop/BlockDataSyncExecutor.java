package zy.pointer.sync.pmeth.loop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import zy.pointer.crps.commons.business.cryptotx.repository.model.EthAddressTag;
import zy.pointer.crps.commons.business.cryptotx.repository.model.EthBlockSyncPM;
import zy.pointer.crps.commons.business.cryptotx.service.IEthAddressTagService;
import zy.pointer.crps.commons.business.cryptotx.service.IEthBlockSyncPMService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

/**
 * 区块数据存储执行器
 */
@Component
@EnableAsync
public class BlockDataSyncExecutor implements InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger( BlockDataSyncExecutor.class );

    @Autowired
    IEthBlockSyncPMService blockSyncPMService;

    @Autowired
    IEthAddressTagService addressTagService;

    private Set<String> addressTagCache ;

    @Value("${eth.sync.height}")
    private Long configSyncHeight;

    @Value("${eth.sync.end-height}")
    private Long configEndHeight;

    public static final BlockingQueue< EthBlockSyncPM > queue = new LinkedBlockingQueue<>();

    @Override
    public void afterPropertiesSet() throws Exception {
//        LOG.info("[##] 初始化交易所地址缓存 ... ");
//        addressTagCache = addressTagService.getAllAddressTag()
//                .stream()
//                .map( EthAddressTag::getAddress )
//                .collect(Collectors.toSet());
//        LOG.info("[##] 初始化交易所地址缓存完成 , Set.size = {}" , addressTagCache.size());
        addressTagCache = new HashSet<>();
    }

    @Scheduled( cron = "0/5 * * * * ?" )
    public void loop(){
        LOG.info("执行未分表写入区块数据查询.");
        List<EthBlockSyncPM> list = blockSyncPMService.getUnSync( configSyncHeight , configEndHeight );
        if ( !list.isEmpty() ){
            LOG.info("本轮未分表写入区块数据范围:{} ~ {}" , list.get(0).getBlockHeight() , list.get( list.size()-1 ).getBlockHeight() );
        }
        list.parallelStream().forEach( ethBlockSyncPM -> {
            LOG.info("写入区块交易数据到分表 - blockHeight:{}" , ethBlockSyncPM.getBlockHeight());
            blockSyncPMService.saveBatchAddressTx( ethBlockSyncPM , addressTagCache );
        } );
        if ( addressTagCache.size() > 100000 ){
            addressTagCache.clear();
        }
    }

}
