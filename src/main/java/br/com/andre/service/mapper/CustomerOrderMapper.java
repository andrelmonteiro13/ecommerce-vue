package br.com.andre.service.mapper;

import br.com.andre.domain.Address;
import br.com.andre.domain.Customer;
import br.com.andre.domain.CustomerOrder;
import br.com.andre.domain.User;
import br.com.andre.service.dto.AddressDTO;
import br.com.andre.service.dto.CustomerDTO;
import br.com.andre.service.dto.CustomerOrderDTO;
import br.com.andre.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CustomerOrder} and its DTO {@link CustomerOrderDTO}.
 */
@Mapper(componentModel = "spring")
public interface CustomerOrderMapper extends EntityMapper<CustomerOrderDTO, CustomerOrder> {
    @Mapping(target = "customer", source = "customer", qualifiedByName = "customerEmail")
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    @Mapping(target = "shippingAddress", source = "shippingAddress", qualifiedByName = "addressId")
    CustomerOrderDTO toDto(CustomerOrder s);

    @Named("customerEmail")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "email", source = "email")
    CustomerDTO toDtoCustomerEmail(Customer customer);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("addressId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AddressDTO toDtoAddressId(Address address);
}
