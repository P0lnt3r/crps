package zy.pointer.crps.commons.framework.web.model.vo;

public interface ValueObject<Entity> {

    <ValueObject> ValueObject from( Entity entity , Class<ValueObject> clazz );

    <ValueObject> ValueObject from( Entity entity , Class<ValueObject> clazz , IConvertHandler<Entity , ValueObject> handler );

}
