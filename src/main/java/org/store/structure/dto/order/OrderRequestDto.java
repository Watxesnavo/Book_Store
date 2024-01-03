package org.store.structure.dto.order;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class OrderRequestDto {
    @NotEmpty
    private String shippingAddress;
}
