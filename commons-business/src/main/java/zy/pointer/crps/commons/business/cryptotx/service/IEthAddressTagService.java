package zy.pointer.crps.commons.business.cryptotx.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import zy.pointer.crps.commons.business.cryptotx.repository.model.EthAddressTag;
import zy.pointer.crps.commons.framework.business.BusinessService;

import java.util.List;

public interface IEthAddressTagService extends BusinessService<EthAddressTag> {

    /**
     * 查询所有地址
     * @return
     */
    List<EthAddressTag> getAllAddressTag();

    IPage<EthAddressTag> getPageQuery( IPage page , String key );

}
