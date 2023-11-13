package org.store.structure.dto.shoppingcart;

import java.util.Set;
import lombok.Data;
import org.store.structure.dto.cartitem.CartItemResponseDto;

@Data
public class ShoppingCartResponseDto {
    private Long id;
    private Long userId;
    private Set<CartItemResponseDto> cartItems;
}
