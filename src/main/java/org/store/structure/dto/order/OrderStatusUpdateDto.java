package org.store.structure.dto.order;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class OrderStatusUpdateDto {
    @NotEmpty
    private String status;
}
