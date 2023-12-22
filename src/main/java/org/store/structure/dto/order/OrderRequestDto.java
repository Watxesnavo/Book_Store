package org.store.structure.dto.order;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class OrderRequestDto {
    @NotEmpty
    @NotNull
    private String shippingAddress;
}
