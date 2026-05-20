package br.com.andre.service.mapper;

import br.com.andre.domain.CustomerOrder;
import br.com.andre.domain.Payment;
import br.com.andre.service.dto.CustomerOrderDTO;
import br.com.andre.service.dto.PaymentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Payment} and its DTO {@link PaymentDTO}.
 */
@Mapper(componentModel = "spring")
public interface PaymentMapper extends EntityMapper<PaymentDTO, Payment> {
    @Mapping(target = "order", source = "order", qualifiedByName = "customerOrderOrderNumber")
    PaymentDTO toDto(Payment s);

    @Named("customerOrderOrderNumber")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "orderNumber", source = "orderNumber")
    CustomerOrderDTO toDtoCustomerOrderOrderNumber(CustomerOrder customerOrder);
}
