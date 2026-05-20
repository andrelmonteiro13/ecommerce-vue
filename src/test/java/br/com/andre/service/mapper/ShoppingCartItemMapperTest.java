package br.com.andre.service.mapper;

import static br.com.andre.domain.ShoppingCartItemAsserts.*;
import static br.com.andre.domain.ShoppingCartItemTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ShoppingCartItemMapperTest {

    private ShoppingCartItemMapper shoppingCartItemMapper;

    @BeforeEach
    void setUp() {
        shoppingCartItemMapper = new ShoppingCartItemMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getShoppingCartItemSample1();
        var actual = shoppingCartItemMapper.toEntity(shoppingCartItemMapper.toDto(expected));
        assertShoppingCartItemAllPropertiesEquals(expected, actual);
    }
}
