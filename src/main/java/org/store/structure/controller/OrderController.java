package org.store.structure.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.store.structure.dto.order.OrderRequestDto;
import org.store.structure.dto.order.OrderResponseDto;
import org.store.structure.dto.order.OrderStatusUpdateDto;
import org.store.structure.dto.orderitem.OrderItemResponseDto;
import org.store.structure.service.order.OrderService;

@Tag(name = "Order management", description = "Endpoints to manage orders")
@RestController
@RequestMapping(value = "/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "place order", description = "create a new order in db")
    public OrderResponseDto placeNewOrder(@RequestBody OrderRequestDto requestDto) {
        return orderService.placeOrder(requestDto);
    }

    @GetMapping
    @Operation(summary = "get all user orders",
            description = "show all the orders that created a user")
    public List<OrderResponseDto> getOrderHistory() {
        return orderService.getOrderHistory();
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public OrderResponseDto updateStatus(@PathVariable Long id,
                                         @RequestBody OrderStatusUpdateDto request) {
        return orderService.updateStatus(id, request);
    }

    @GetMapping("/{orderId}/items")
    public Set<OrderItemResponseDto> getItemsForOrder(@PathVariable Long orderId) {
        return orderService.getItemsForOrder(orderId);
    }

    @GetMapping("{orderId}/items/{itemId}")
    public OrderItemResponseDto getSpecificItemFromOrder(@PathVariable Long orderId,
                                                         @PathVariable Long itemId) {
        return orderService.getSpecificItemFromOrder(orderId, itemId);
    }
}
