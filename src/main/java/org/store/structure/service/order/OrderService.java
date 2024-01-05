package org.store.structure.service.order;

import java.util.List;
import java.util.Set;
import org.store.structure.dto.order.OrderRequestDto;
import org.store.structure.dto.order.OrderResponseDto;
import org.store.structure.dto.order.OrderStatusUpdateDto;
import org.store.structure.dto.orderitem.OrderItemResponseDto;
import org.store.structure.model.User;

public interface OrderService {
    OrderResponseDto placeOrder(OrderRequestDto requestDto, User user);

    List<OrderResponseDto> getOrderHistory(User user);

    OrderResponseDto updateStatus(Long orderId, OrderStatusUpdateDto request);

    Set<OrderItemResponseDto> getItemsForOrder(Long orderId);

    OrderItemResponseDto getSpecificItemFromSpecificOrder(Long orderId, Long itemId);
}
