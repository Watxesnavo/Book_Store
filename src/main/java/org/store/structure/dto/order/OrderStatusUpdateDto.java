package org.store.structure.dto.order;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderStatusUpdateDto {
    @NotEmpty
    @NotNull
    private String status;
}
