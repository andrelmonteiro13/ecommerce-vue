package br.com.andre.service.mapper;

import br.com.andre.domain.CustomerOrder;
import br.com.andre.domain.Shipment;
import br.com.andre.service.dto.CustomerOrderDTO;
import br.com.andre.service.dto.ShipmentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Shipment} and its DTO {@link ShipmentDTO}.
 */
@Mapper(componentModel = "spring")
public interface ShipmentMapper extends EntityMapper<ShipmentDTO, Shipment> {
    @Mapping(target = "order", source = "order", qualifiedByName = "customerOrderOrderNumber")
    ShipmentDTO toDto(Shipment s);

    @Named("customerOrderOrderNumber")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "orderNumber", source = "orderNumber")
    CustomerOrderDTO toDtoCustomerOrderOrderNumber(CustomerOrder customerOrder);
}
