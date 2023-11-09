package org.store.structure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.store.structure.config.MapperConfig;
import org.store.structure.dto.shoppingcart.ShoppingCartResponseDto;
import org.store.structure.model.ShoppingCart;

@Mapper(config = MapperConfig.class,
        uses = {CartItemMapper.class, BookMapper.class}
)
public interface ShoppingCartMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "cartItems", target = "cartItems")
    ShoppingCartResponseDto toDto(ShoppingCart shoppingCart);
}
