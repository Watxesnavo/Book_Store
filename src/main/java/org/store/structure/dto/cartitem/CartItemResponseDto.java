package org.store.structure.dto.cartitem;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CartItemResponseDto {
    private Long id;
    private Long shoppingCartId;
    private Long bookId;
    private String bookTitle;
    private int quantity;
}
