package org.store.structure.dto.shopping_cart;

import java.util.Set;
import lombok.Data;
import org.store.structure.dto.cart_item.CartItemRequestDto;

@Data
public class ShoppingCartRequestDto {
    private Long userId;
    private Set<CartItemRequestDto> cartItems;
}
