package org.store.structure.service.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.store.structure.dto.order.OrderRequestDto;
import org.store.structure.dto.order.OrderResponseDto;
import org.store.structure.dto.order.OrderStatusUpdateDto;
import org.store.structure.dto.orderitem.OrderItemResponseDto;
import org.store.structure.exception.EntityNotFoundException;
import org.store.structure.mapper.OrderItemMapper;
import org.store.structure.mapper.OrderMapper;
import org.store.structure.model.CartItem;
import org.store.structure.model.Order;
import org.store.structure.model.OrderItem;
import org.store.structure.model.ShoppingCart;
import org.store.structure.model.User;
import org.store.structure.repository.book.BookRepository;
import org.store.structure.repository.order.OrderRepository;
import org.store.structure.repository.orderitem.OrderItemRepository;
import org.store.structure.repository.shoppingcart.ShoppingCartRepository;
import org.store.structure.repository.user.UserRepository;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final BookRepository bookRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public OrderResponseDto placeOrder(OrderRequestDto requestDto, UserDetails user) {
        Order order = saveDefaultOrder(user);
        saveAllItems(order, user);
        order.setShippingAddress(requestDto.getShippingAddress());
        order.setOrderItems(orderItemRepository.findAllByOrderUserEmail(user.getUsername()));
        order.setTotal(getItemsTotal(user));
        return orderMapper.toDto(order);
    }

    @Override
    public List<OrderResponseDto> getOrderHistory(UserDetails user) {
        List<Order> orders = orderRepository.findAllByUserEmail(
                user.getUsername()
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
    public OrderItemResponseDto getSpecificItemFromSpecificOrder(Long orderId, Long itemId) {
        return orderItemMapper.toDto(orderItemRepository.findByOrderIdAndId(orderId, itemId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find item by id: " + itemId + " or order id: " + orderId))
        );
    }

    private BigDecimal getItemsTotal(UserDetails user) {
        Set<OrderItem> orderItems = orderItemRepository.findAllByOrderUserEmail(user.getUsername());
        List<BigDecimal> prices = orderItems.stream()
                .map(OrderItem::getPrice)
                .toList();
        return prices.stream().reduce(BigDecimal.ZERO,BigDecimal::add);
    }

    private void saveAllItems(Order order, UserDetails user) {
        ShoppingCart currentCart = shoppingCartRepository.findFirstByUserEmail(user.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Can't find a cart by email: "
                        + user.getUsername()));
        Set<CartItem> cartItems = currentCart.getCartItems();
        Set<OrderItem> orderItems = transformCartToOrderItems(cartItems, order);
        if (!orderItems.containsAll(orderItemRepository.findAll())) {
            orderItemRepository.saveAll(orderItems);
        }
    }

    private Set<OrderItem> transformCartToOrderItems(Set<CartItem> cartItems, Order order) {
        Set<OrderItem> orderItems = new HashSet<>();
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setBook(cartItem.getBook());
            orderItem.setPrice(bookRepository.findById(
                            cartItem.getBook().getId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Can't find a book by id " + cartItem.getBook().getId())
                    )
                    .getPrice());
            orderItem.setOrder(order);
            orderItems.add(orderItem);
        }
        return orderItems;
    }

    private Order saveDefaultOrder(UserDetails userDetails) {
        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Order.Status.CREATED);
        order.setUser(getCurrentUser(userDetails));
        order.setShippingAddress(getCurrentUser(userDetails).getShippingAddress());
        order.setTotal(BigDecimal.ZERO);
        return orderRepository.save(order);
    }

    private User getCurrentUser(UserDetails user) {
        return userRepository.findByEmail(user.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Can't find a user by email"));
    }

    private Order getCurrentOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Can't find order by id: " + orderId)
                );
    }
}
