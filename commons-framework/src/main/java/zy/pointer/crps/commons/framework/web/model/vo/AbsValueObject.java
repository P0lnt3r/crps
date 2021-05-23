package zy.pointer.crps.commons.framework.web.model.vo;

import cn.hutool.core.bean.BeanUtil;
import zy.pointer.crps.commons.framework.repository.BaseEntity;

public abstract class AbsValueObject<Entity extends BaseEntity> implements ValueObject<Entity> {

    @Override
    public <ValueObject> ValueObject from(Entity entity, Class<ValueObject> clazz) {
        BeanUtil.copyProperties( entity , this );
        return (ValueObject) this;
    }

    @Override
    public <ValueObject> ValueObject from(Entity entity, Class<ValueObject> clazz, IConvertHandler<Entity, ValueObject> handler) {
        ValueObject vo = (ValueObject)this;
        if ( handler != null ){
            handler.handle(entity , vo);
            return vo;
        }else{
            return from( entity , clazz );
        }
    }


}
