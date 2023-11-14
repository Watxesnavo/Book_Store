package org.store.structure.service.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.store.structure.dto.order.OrderRequestDto;
import org.store.structure.dto.order.OrderResponseDto;
import org.store.structure.dto.order.OrderStatusUpdateDto;
import org.store.structure.dto.orderitem.OrderItemResponseDto;
import org.store.structure.exception.EntityNotFoundException;
import org.store.structure.mapper.OrderItemMapper;
import org.store.structure.mapper.OrderMapper;
import org.store.structure.model.Order;
import org.store.structure.repository.order.OrderRepository;
import org.store.structure.service.orderitem.OrderItemService;
import org.store.structure.service.user.UserService;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final OrderRepository orderRepository;
    private final OrderItemService orderItemService;
    private final UserService userService;

    @Override
    @Transactional
    public OrderResponseDto placeOrder(OrderRequestDto requestDto) {
        Order order = saveDefaultOrder(LocalDateTime.now());
        orderItemService.saveAllItems();
        order.setShippingAddress(requestDto.getShippingAddress());
        order.setOrderItems(orderItemService.getOrderItems());
        order.setTotal(orderItemService.getItemsTotal());
        return orderMapper.toDto(order);
    }

    @Override
    public List<OrderResponseDto> getOrderHistory() {
        List<Order> orders = orderRepository.findAllByUserEmail(
                userService.getCurrentUser().getEmail()
        );
        return orders.stream().map(orderMapper::toDto).toList();
    }

    @Override
    @Transactional
    public OrderResponseDto updateStatus(Long orderId, OrderStatusUpdateDto request) {
        Order order = getCurrentOrderById(orderId);
        order.setStatus(Order.Status.valueOf(request.getStatus()));
        return orderMapper.toDto(order);
    }

    @Override
    public Set<OrderItemResponseDto> getItemsForOrder(Long orderId) {
        Order order = getCurrentOrderById(orderId);
        return order.getOrderItems().stream()
                .map(orderItemMapper::toDto)
                .collect(Collectors.toSet());
    }

    @Override
    public OrderItemResponseDto getSpecificItemFromOrder(Long orderId, Long itemId) {
        Order order = getCurrentOrderById(orderId);
        return orderItemMapper.toDto(order.getOrderItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst().orElseThrow(
                        () -> new EntityNotFoundException("Can't find item after filtering"))
        );
    }

    private Order saveDefaultOrder(LocalDateTime date) {
        Order order = new Order();
        order.setOrderDate(date);
        order.setStatus(Order.Status.CREATED);
        order.setUser(userService.getCurrentUser());
        order.setShippingAddress(userService.getCurrentUser().getShippingAddress());
        order.setTotal(BigDecimal.ZERO);
        return orderRepository.save(order);
    }

    private Order getCurrentOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Can't find order by id: " + orderId)
                );
    }
}
