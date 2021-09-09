package zy.pointer.crps.commons.business.cryptotx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zy.pointer.crps.commons.business.cryptotx.repository.mapper.TxMonitorLogMapper;
import zy.pointer.crps.commons.business.cryptotx.repository.model.TxMonitorLog;
import zy.pointer.crps.commons.business.cryptotx.service.ITxMonitorLogService;
import zy.pointer.crps.commons.framework.business.AbsBusinessService;

@Service
@Primary
@Transactional
public class TxMonitorLogServiceImpl extends AbsBusinessService<TxMonitorLogMapper , TxMonitorLog> implements ITxMonitorLogService {

    @Override
    public IPage<TxMonitorLog> getPageQuery(IPage page, Long refStrategyId) {
        LambdaQueryWrapper< TxMonitorLog > queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq( TxMonitorLog::getRefStrategyId , refStrategyId );
        return getBaseMapper().selectPage( page , queryWrapper );
    }
}
