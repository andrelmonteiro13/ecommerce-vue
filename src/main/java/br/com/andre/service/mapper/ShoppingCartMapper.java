package br.com.andre.service.mapper;

import br.com.andre.domain.Customer;
import br.com.andre.domain.ShoppingCart;
import br.com.andre.service.dto.CustomerDTO;
import br.com.andre.service.dto.ShoppingCartDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ShoppingCart} and its DTO {@link ShoppingCartDTO}.
 */
@Mapper(componentModel = "spring")
public interface ShoppingCartMapper extends EntityMapper<ShoppingCartDTO, ShoppingCart> {
    @Mapping(target = "customer", source = "customer", qualifiedByName = "customerEmail")
    ShoppingCartDTO toDto(ShoppingCart s);

    @Named("customerEmail")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "email", source = "email")
    CustomerDTO toDtoCustomerEmail(Customer customer);
}
