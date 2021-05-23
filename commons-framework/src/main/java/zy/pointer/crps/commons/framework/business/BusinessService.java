package zy.pointer.crps.commons.framework.business;


import com.baomidou.mybatisplus.extension.service.IService;
import zy.pointer.crps.commons.framework.repository.BaseEntity;
import zy.pointer.crps.commons.framework.repository.ExtendsMapper;

/**
 * 基础业务接口
 *      继承 mybatis-plus IService 接口 , 即顶级业务接口
 * @param <Entity>
 */
public interface BusinessService<Entity extends BaseEntity> extends IService<Entity> , TopService, ExtendsMapper<Entity> {



}
