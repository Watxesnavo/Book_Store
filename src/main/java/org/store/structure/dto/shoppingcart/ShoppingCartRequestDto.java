package org.store.structure.dto.shoppingcart;

import jakarta.validation.constraints.NotNull;
import java.util.Set;
import lombok.Data;
import org.store.structure.dto.cartitem.CartItemRequestDto;

@Data
public class ShoppingCartRequestDto {
    @NotNull
    private Long userId;
    @NotNull
    private Set<CartItemRequestDto> cartItems;
}
