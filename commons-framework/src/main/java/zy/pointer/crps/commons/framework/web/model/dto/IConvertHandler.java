package zy.pointer.crps.commons.framework.web.model.dto;

/**
 * DTO 自定义转换 Entity 实现
 * @param <DataTransferObject>
 * @param <Entity>
 */
public interface IConvertHandler<DataTransferObject , Entity> {

    Entity handle( DataTransferObject dto , Entity entity);

}
