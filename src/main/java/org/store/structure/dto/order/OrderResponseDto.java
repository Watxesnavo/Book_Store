package org.store.structure.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Data;
import lombok.experimental.Accessors;
import org.store.structure.dto.orderitem.OrderItemResponseDto;
import org.store.structure.model.Order;

@Data
@Accessors(chain = true)
public class OrderResponseDto {
    private Long id;
    private Long userId;
    private Set<OrderItemResponseDto> orderItems;
    private LocalDateTime orderDate;
    private BigDecimal total;
    private Order.Status status;
}
