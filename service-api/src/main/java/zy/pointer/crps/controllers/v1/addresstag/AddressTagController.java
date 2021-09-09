package zy.pointer.crps.controllers.v1.addresstag;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zy.pointer.crps.commons.business.cryptotx.repository.model.EthAddressTag;
import zy.pointer.crps.commons.business.cryptotx.service.IEthAddressTagService;
import zy.pointer.crps.commons.framework.web.model.vo.PageResponseVO;

@Api( tags = "地址标签" )
@RestController
@RequestMapping("/api/v1/addresstag")
public class AddressTagController {

    @Autowired
    private IEthAddressTagService ethAddressTagService;

    @ApiOperation(value = "获取地址标签数据")
    @GetMapping("/query")
    public PageResponseVO< EthAddressTagVO , EthAddressTag> pageQuery( @Validated AddressTagQueryDTO dto ){
        String key = dto.getKey();
        IPage<EthAddressTag> page = ethAddressTagService.getPageQuery( dto.convert() , key );
        return new PageResponseVO< EthAddressTagVO , EthAddressTag >().from( page , EthAddressTagVO.class );
    }

}
