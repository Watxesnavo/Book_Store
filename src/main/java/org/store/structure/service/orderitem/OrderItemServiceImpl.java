package org.store.structure.service.orderitem;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.store.structure.model.CartItem;
import org.store.structure.model.Order;
import org.store.structure.model.OrderItem;
import org.store.structure.model.ShoppingCart;
import org.store.structure.repository.orderitem.OrderItemRepository;
import org.store.structure.service.book.BookService;
import org.store.structure.service.shoppingcart.ShoppingCartService;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final ShoppingCartService shoppingCartService;
    private final BookService bookService;

    @Override
    public void saveAllItems(Order order, UserDetails user) {
        ShoppingCart currentCart = shoppingCartService.getCurrentCart(user);
        Set<CartItem> cartItems = currentCart.getCartItems();
        Set<OrderItem> orderItems = transformCartToOrderItems(cartItems, order);
        if (!orderItems.containsAll(orderItemRepository.findAll())) {
            orderItemRepository.saveAll(orderItems);
        }
    }

    @Override
    public Set<OrderItem> getOrderItems(UserDetails user) {
        return orderItemRepository.findAllByOrderUserEmail(user.getUsername());
    }

    @Override
    public BigDecimal getItemsTotal(UserDetails user) {
        Set<OrderItem> orderItems = getOrderItems(user);
        List<BigDecimal> prices = orderItems.stream()
                .map(OrderItem::getPrice)
                .toList();
        return prices.stream().reduce(BigDecimal.ZERO,BigDecimal::add);
    }

    private Set<OrderItem> transformCartToOrderItems(Set<CartItem> cartItems, Order order) {
        Set<OrderItem> orderItems = new HashSet<>();
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setBook(cartItem.getBook());
            orderItem.setPrice(bookService.findById(cartItem.getBook().getId()).getPrice());
            orderItem.setOrder(order);
            orderItems.add(orderItem);
        }
        return orderItems;
    }
}
