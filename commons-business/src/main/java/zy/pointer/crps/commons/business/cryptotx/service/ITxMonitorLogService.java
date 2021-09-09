package zy.pointer.crps.commons.business.cryptotx.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import zy.pointer.crps.commons.business.cryptotx.repository.model.TxMonitorLog;
import zy.pointer.crps.commons.framework.business.BusinessService;

public interface ITxMonitorLogService extends BusinessService<TxMonitorLog> {

    IPage< TxMonitorLog > getPageQuery( IPage page , Long refStrategyId );

}
