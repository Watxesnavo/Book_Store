package org.store.structure.dto.cartitem;

import lombok.Data;

@Data
public class CartItemRequestDto {
    private Long bookId;
    private int quantity;
}
