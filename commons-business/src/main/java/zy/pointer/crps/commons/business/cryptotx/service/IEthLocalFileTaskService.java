package zy.pointer.crps.commons.business.cryptotx.service;

import zy.pointer.crps.commons.business.cryptotx.repository.model.EthLocalFileTask;
import zy.pointer.crps.commons.framework.business.BusinessService;

import java.util.List;

public interface IEthLocalFileTaskService extends BusinessService <EthLocalFileTask> {

    List< EthLocalFileTask > getUnExecuteFileTask( String source );

    void handleSyncLocalFile( EthLocalFileTask ethLocalFileTask , int position , String line );

}
