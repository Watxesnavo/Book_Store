package org.store.structure.service.order;

import java.util.List;
import java.util.Set;
import org.springframework.security.core.userdetails.UserDetails;
import org.store.structure.dto.order.OrderRequestDto;
import org.store.structure.dto.order.OrderResponseDto;
import org.store.structure.dto.order.OrderStatusUpdateDto;
import org.store.structure.dto.orderitem.OrderItemResponseDto;

public interface OrderService {
    OrderResponseDto placeOrder(OrderRequestDto requestDto, UserDetails user);

    List<OrderResponseDto> getOrderHistory(UserDetails user);

    OrderResponseDto updateStatus(Long orderId, OrderStatusUpdateDto request);

    Set<OrderItemResponseDto> getItemsForOrder(Long orderId);

    OrderItemResponseDto getSpecificItemFromSpecificOrder(Long orderId, Long itemId);
}
