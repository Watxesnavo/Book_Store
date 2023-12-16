package org.store.structure.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import org.store.structure.model.User;
import org.store.structure.service.order.OrderService;

@Tag(name = "Order management", description = "Endpoints to manage orders")
@RestController
@RequestMapping(value = "/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "place order", description = "create a new order in db")
    public ResponseEntity<OrderResponseDto> placeNewOrder(
            @RequestBody @Valid OrderRequestDto requestDto,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderService.placeOrder(requestDto, user));
    }

    @GetMapping
    @Operation(summary = "get all user orders",
            description = "show all the orders that created a user")
    public ResponseEntity<List<OrderResponseDto>> getOrderHistory(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getOrderHistory(user));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<OrderResponseDto> updateStatus(@PathVariable Long id,
                                         @RequestBody @Valid OrderStatusUpdateDto request) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.updateStatus(id, request));
    }

    @GetMapping("/{orderId}/items")
    public ResponseEntity<Set<OrderItemResponseDto>> getItemsForOrder(@PathVariable Long orderId) {
        return ResponseEntity.status(HttpStatus.FOUND).body(orderService.getItemsForOrder(orderId));
    }

    @GetMapping("{orderId}/items/{itemId}")
    public ResponseEntity<OrderItemResponseDto> getSpecificItemFromOrder(@PathVariable Long orderId,
                                                         @PathVariable Long itemId) {
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .body(orderService.getSpecificItemFromSpecificOrder(orderId, itemId));
    }
}
