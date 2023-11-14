package org.store.structure.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
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
    public ShoppingCartResponseDto getCurrentShoppingCart(UserDetails user) {
        return shoppingCartMapper.toDto(shoppingCartService.getCurrentCart(user));
    }

    @PostMapping
    @Operation(summary = "add book to the cart", description = "add new item to the cart")
    public ShoppingCartResponseDto addBook(
            @RequestBody CartItemRequestDto requestDto, UserDetails user
    ) {
        return shoppingCartService.addBook(requestDto, user);
    }

    @PutMapping("/cart-items/{itemId}")
    @Operation(summary = "update item", description = "update quantity of item")
    public ShoppingCartResponseDto updateItem(@PathVariable Long itemId,
                                              @RequestBody CartItemUpdateDto dto,
                                              UserDetails user) {
        return shoppingCartService.updateItem(itemId, dto, user);
    }

    @DeleteMapping("/cart-items/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "delete item from cart",
            description = "soft delete item, and delete item from current cart")
    public ShoppingCartResponseDto deleteItem(@PathVariable Long id, UserDetails user) {
        return shoppingCartService.deleteItem(id, user);
    }
}
