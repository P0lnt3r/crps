package cryptotx;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import test.SpringTestCase;
import zy.pointer.crps.commons.business.cryptotx.repository.model.EthAddressTag;
import zy.pointer.crps.commons.business.cryptotx.service.IEthAddressTagService;
import zy.pointer.crps.commons.framework.web.model.vo.PageResponseVO;
import zy.pointer.crps.controllers.v1.addresstag.AddressTagQueryDTO;
import zy.pointer.crps.controllers.v1.addresstag.EthAddressTagVO;

public class AddressTagServiceTest extends SpringTestCase {

    @Autowired
    IEthAddressTagService addressTagService;

    @Test
    public void test(){
        EthAddressTag addressTag = new EthAddressTag();
        addressTag.setAddress("0x5a959630caa889776625257c87fe69166aa339b7");
        addressTag.setTag("火币");
        addressTagService.save( addressTag );
    }

    @Test
    public void testPage(){
        AddressTagQueryDTO query = new AddressTagQueryDTO();
        query.setKey("Huobi");
        query.setPage(1L);
        query.setSize(20L);
        LambdaQueryWrapper< EthAddressTag > queryWrapper = Wrappers.lambdaQuery();
        IPage<EthAddressTag> page = addressTagService.getPageQuery( query.convert() , query.getKey() );
        PageResponseVO pageVo = new PageResponseVO<EthAddressTagVO , EthAddressTag>().from( page , EthAddressTagVO.class );
        System.out.println( pageVo );
    }

}
