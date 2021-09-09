package zy.pointer.crps.controllers.v1.txmonitor;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zy.pointer.crps.commons.business.cryptotx.DictConstant;
import zy.pointer.crps.commons.business.cryptotx.repository.model.TxMonitorLog;
import zy.pointer.crps.commons.business.cryptotx.repository.model.TxMonitorStrategy;
import zy.pointer.crps.commons.business.cryptotx.service.ITxMonitorLogService;
import zy.pointer.crps.commons.business.cryptotx.service.ITxMonitorStrategyService;
import zy.pointer.crps.commons.framework.web.model.vo.PageResponseVO;
import zy.pointer.crps.commons.utils.DateUtil;

import java.time.LocalDateTime;

@Api( tags = "交易监控" )
@RestController
@RequestMapping("/api/v1/txmonitor")
public class TxMonitorController {

    @Autowired
    ITxMonitorStrategyService txMonitorStrategyService;

    @Autowired
    ITxMonitorLogService txMonitorLogService;

    @GetMapping( "/query" )
    @ApiOperation(value = "获取监控策略分页数据")
    public PageResponseVO< TxMonitorStrategyVO , TxMonitorStrategy> query(TxMonitorStrategyPageQueryDTO dto){
        String title = dto.getTitle();
        String address = dto.getAddress();
        return new PageResponseVO< TxMonitorStrategyVO , TxMonitorStrategy >().from(
                txMonitorStrategyService.getPageQuery( dto.convert() , address , title ),
                TxMonitorStrategyVO.class ,
                ( txMonitorStrategy , vo ) -> {
                    vo.setCreateTime( DateUtil.getDateString( txMonitorStrategy.getCreateTime() ) );
                    String flowType = txMonitorStrategy.getFlowType();
                    String strategy = "";
                    String amount = txMonitorStrategy.getAmount();
                    if (DictConstant.CRYPTO_ADDRESS_TX_FLOWTYPE_IN.value.equals( flowType )){
                        strategy += "转入";
                    }else if ( DictConstant.CRYPTO_ADDRESS_TX_FLOWTYPE_OUT.value.equals( flowType ) ){
                        strategy += "转出";
                    }else if( DictConstant.CRYPTO_ADDRESS_TX_FLOWTYPE_IN_OUT.value.equals( flowType ) ){
                        strategy += "转入或转出";
                    }
                    strategy += " >=" + amount + " " + txMonitorStrategy.getToken();
                    vo.setStrategy( strategy );
                    vo.setMainNet("ETH ");
                    return vo;
                }
        );
    }

    @GetMapping( "/queryLog" )
    @ApiOperation( value = "获取监控日志分页数据" )
    public PageResponseVO<TxMonitorLogVo , TxMonitorLog> queryLog( TxMonitorLogPageQueryDTO dto ){
        Long refStrategyId = dto.getRefStrategyId();
        return new PageResponseVO< TxMonitorLogVo , TxMonitorLog>().from(
                txMonitorLogService.getPageQuery( dto.convert() , dto.getRefStrategyId() ) ,
                TxMonitorLogVo.class
        );
    }


}
