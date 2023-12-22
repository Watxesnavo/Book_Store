package org.store.structure.dto.cartitem;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class CartItemUpdateDto {
    @NotNull
    @Min(1)
    private int quantity;
}
