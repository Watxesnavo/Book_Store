package org.store.structure.dto.cart_item;

import lombok.Data;

@Data
public class CartItemResponseDto {
    private Long id;
    private Long shoppingCartId;
    private Long bookId;
    private String bookTitle;
    private int quantity;
}
