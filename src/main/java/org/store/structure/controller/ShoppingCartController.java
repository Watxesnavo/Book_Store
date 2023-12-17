package org.store.structure.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.store.structure.dto.cartitem.CartItemRequestDto;
import org.store.structure.dto.cartitem.CartItemUpdateDto;
import org.store.structure.dto.shoppingcart.ShoppingCartResponseDto;
import org.store.structure.mapper.ShoppingCartMapper;
import org.store.structure.model.User;
import org.store.structure.service.shoppingcart.ShoppingCartService;

@Tag(name = "Shopping Carts management", description = "Endpoints to manage shopping carts")
@RestController
@RequestMapping(value = "/cart")
@RequiredArgsConstructor
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;
    private final ShoppingCartMapper shoppingCartMapper;

    @GetMapping
    @Operation(summary = "show current cart",
            description = "show the current logged user shopping cart")
    public ResponseEntity<ShoppingCartResponseDto> getCurrentShoppingCart(
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(shoppingCartMapper.toDto(shoppingCartService.getCurrentCart(user)));
    }

    @PostMapping
    @Operation(summary = "add book to the cart", description = "add new item to the cart")
    public ResponseEntity<ShoppingCartResponseDto> addBook(
            @RequestBody @Valid CartItemRequestDto requestDto, @AuthenticationPrincipal User user
    ) {
        return new ResponseEntity<>(shoppingCartService.addBook(requestDto, user), HttpStatus.OK);
    }

    @PutMapping("/cart-items/{itemId}")
    @Operation(summary = "update item", description = "update quantity of item")
    public ResponseEntity<ShoppingCartResponseDto> updateItem(@PathVariable Long itemId,
                                              @RequestBody @Valid CartItemUpdateDto dto,
                                                              @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(shoppingCartService.updateItemQuantity(itemId, dto, user));
    }

    @DeleteMapping("/cart-items/{id}")
    @Operation(summary = "delete item from cart",
            description = "soft delete item, and delete item from current cart")
    public ResponseEntity<ShoppingCartResponseDto> deleteItem(@PathVariable Long id,
                                                              @AuthenticationPrincipal User user) {
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(shoppingCartService.deleteItem(id, user));
    }
}
