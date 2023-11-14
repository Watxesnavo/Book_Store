package org.store.structure.service.order;

import java.util.List;
import java.util.Set;
import org.store.structure.dto.order.OrderRequestDto;
import org.store.structure.dto.order.OrderResponseDto;
import org.store.structure.dto.order.OrderStatusUpdateDto;
import org.store.structure.dto.orderitem.OrderItemResponseDto;

public interface OrderService {
    OrderResponseDto placeOrder(OrderRequestDto requestDto);

    List<OrderResponseDto> getOrderHistory();

    OrderResponseDto updateStatus(Long orderId, OrderStatusUpdateDto request);

    Set<OrderItemResponseDto> getItemsForOrder(Long orderId);

    OrderItemResponseDto getSpecificItemFromOrder(Long orderId, Long itemId);
}
