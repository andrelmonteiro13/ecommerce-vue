package br.com.andre.service.mapper;

import br.com.andre.domain.Product;
import br.com.andre.domain.ShoppingCart;
import br.com.andre.domain.ShoppingCartItem;
import br.com.andre.service.dto.ProductDTO;
import br.com.andre.service.dto.ShoppingCartDTO;
import br.com.andre.service.dto.ShoppingCartItemDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ShoppingCartItem} and its DTO {@link ShoppingCartItemDTO}.
 */
@Mapper(componentModel = "spring")
public interface ShoppingCartItemMapper extends EntityMapper<ShoppingCartItemDTO, ShoppingCartItem> {
    @Mapping(target = "cart", source = "cart", qualifiedByName = "shoppingCartId")
    @Mapping(target = "product", source = "product", qualifiedByName = "productName")
    ShoppingCartItemDTO toDto(ShoppingCartItem s);

    @Named("shoppingCartId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ShoppingCartDTO toDtoShoppingCartId(ShoppingCart shoppingCart);

    @Named("productName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ProductDTO toDtoProductName(Product product);
}
