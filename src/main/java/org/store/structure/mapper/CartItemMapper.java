package org.store.structure.mapper;

import org.mapstruct.Mapper;
import org.store.structure.config.MapperConfig;
import org.store.structure.dto.cartitem.CartItemResponseDto;
import org.store.structure.model.CartItem;

@Mapper(config = MapperConfig.class
)
public abstract class CartItemMapper {

    public CartItemResponseDto toDto(CartItem cartItem) {
        CartItemResponseDto dto = new CartItemResponseDto();
        dto.setId(cartItem.getId());
        dto.setQuantity(cartItem.getQuantity());
        dto.setShoppingCartId(cartItem.getShoppingCart().getId());
        dto.setBookId(cartItem.getBook().getId());
        dto.setBookTitle(cartItem.getBook().getTitle());
        return dto;
    }
}
