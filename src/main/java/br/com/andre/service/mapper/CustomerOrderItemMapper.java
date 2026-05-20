package br.com.andre.service.mapper;

import br.com.andre.domain.CustomerOrder;
import br.com.andre.domain.CustomerOrderItem;
import br.com.andre.domain.Product;
import br.com.andre.service.dto.CustomerOrderDTO;
import br.com.andre.service.dto.CustomerOrderItemDTO;
import br.com.andre.service.dto.ProductDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CustomerOrderItem} and its DTO {@link CustomerOrderItemDTO}.
 */
@Mapper(componentModel = "spring")
public interface CustomerOrderItemMapper extends EntityMapper<CustomerOrderItemDTO, CustomerOrderItem> {
    @Mapping(target = "order", source = "order", qualifiedByName = "customerOrderOrderNumber")
    @Mapping(target = "product", source = "product", qualifiedByName = "productName")
    CustomerOrderItemDTO toDto(CustomerOrderItem s);

    @Named("customerOrderOrderNumber")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "orderNumber", source = "orderNumber")
    CustomerOrderDTO toDtoCustomerOrderOrderNumber(CustomerOrder customerOrder);

    @Named("productName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ProductDTO toDtoProductName(Product product);
}
