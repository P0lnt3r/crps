package zy.pointer.crps.commons.business.cryptotx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zy.pointer.crps.commons.business.cryptotx.repository.mapper.EthAddressTagMapper;
import zy.pointer.crps.commons.business.cryptotx.repository.model.EthAddressTag;
import zy.pointer.crps.commons.business.cryptotx.service.IEthAddressTagService;
import zy.pointer.crps.commons.framework.business.AbsBusinessService;

import java.util.List;

@Service
@Primary
@Transactional
public class EthAddressTagServiceImpl extends AbsBusinessService<EthAddressTagMapper, EthAddressTag> implements IEthAddressTagService {

    @Override
    public List<EthAddressTag> getAllAddressTag() {
        return getBaseMapper().selectList(null);
    }

    @Override
    public IPage<EthAddressTag> getPageQuery(IPage page, String key) {
        LambdaQueryWrapper< EthAddressTag > queryWrapper = Wrappers.lambdaQuery();
        if ( key != null && key.startsWith("0x") ){
            queryWrapper.eq( EthAddressTag::getAddress , key );
        }else if( key != null && !"".equals( key.trim() ) ) {
            queryWrapper.eq( EthAddressTag::getTag , key );
        }
        return getBaseMapper().selectPage( page , queryWrapper );
    }
}
