package zy.pointer.crps.commons.framework.web.model.dto;

/**
 *
 * @param <Entity>
 */
public interface DataTransferObject<Entity> {

    /**
     * 将接口层接受的数据转换为泛型类型
     * @return 泛型类型对象
     */
    Entity convert();

    /**
     * 通过自定义规则将接口层接受的数据转换为泛型类型
     * @param clazz 当前类型.class
     * @param handler 转行处理对象,自定义实现
     * @return 泛型类型对象
     */
    <DataTransferObject> Entity convert( Class<DataTransferObject> clazz , IConvertHandler< DataTransferObject , Entity> handler );

}
