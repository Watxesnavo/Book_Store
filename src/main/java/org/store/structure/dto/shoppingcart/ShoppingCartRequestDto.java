package org.store.structure.dto.shoppingcart;

import java.util.Set;
import lombok.Data;
import org.store.structure.dto.cartitem.CartItemRequestDto;

@Data
public class ShoppingCartRequestDto {
    private Long userId;
    private Set<CartItemRequestDto> cartItems;
}
