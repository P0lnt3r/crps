package zy.pointer.crps.commons.framework.web.model.vo;

public interface IConvertHandler< Entity , ValueObject > {

    ValueObject handle( Entity entity , ValueObject vo );

}
