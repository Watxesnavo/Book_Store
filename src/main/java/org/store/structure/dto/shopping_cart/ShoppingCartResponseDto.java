package org.store.structure.dto.shopping_cart;

import java.util.Set;
import lombok.Data;
import org.store.structure.dto.cart_item.CartItemResponseDto;

@Data
public class ShoppingCartResponseDto {
    private Long id;
    private Long userId;
    private Set<CartItemResponseDto> cartItems;
}
