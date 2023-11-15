package org.store.structure.service.orderitem;

import java.math.BigDecimal;
import java.util.Set;
import org.springframework.security.core.userdetails.UserDetails;
import org.store.structure.model.Order;
import org.store.structure.model.OrderItem;

public interface OrderItemService {
    void saveAllItems(Order order, UserDetails user);

    Set<OrderItem> getOrderItems(UserDetails user);

    BigDecimal getItemsTotal(UserDetails user);
}
