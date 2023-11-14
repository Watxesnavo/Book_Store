package org.store.structure.service.orderitem;

import java.math.BigDecimal;
import java.util.Set;
import org.store.structure.model.OrderItem;

public interface OrderItemService {
    void saveAllItems();

    Set<OrderItem> getOrderItems();

    BigDecimal getItemsTotal();
}
