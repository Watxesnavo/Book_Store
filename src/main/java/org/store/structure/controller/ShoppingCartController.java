package org.store.structure.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.store.structure.dto.cart_item.CartItemRequestDto;
import org.store.structure.dto.cart_item.CartItemUpdateDto;
import org.store.structure.dto.shopping_cart.ShoppingCartResponseDto;
import org.store.structure.service.shoppingcart.ShoppingCartService;

@Tag(name = "Shopping Carts management", description = "Endpoints to manage shopping carts")
@RestController
@RequestMapping(value = "/cart")
@RequiredArgsConstructor
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @GetMapping
    @Operation(summary = "show current cart",
            description = "show the current logged user shopping cart")
    public ShoppingCartResponseDto getCurrentShoppingCart() {
        return shoppingCartService.getCurrentCart();
    }

    @PostMapping
    @Operation(summary = "add book to the cart", description = "add new item to the cart")
    public ShoppingCartResponseDto addBook(@RequestBody CartItemRequestDto requestDto) {
        return shoppingCartService.addBook(requestDto);
    }

    @PutMapping("/cart-items/{itemId}")
    @Operation(summary = "update item", description = "update quantity of item")
    public ShoppingCartResponseDto updateItem(@PathVariable Long itemId,
                                              @RequestBody CartItemUpdateDto dto) {
        return shoppingCartService.updateItem(itemId, dto);
    }



    @DeleteMapping("/cart-items/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "delete item from cart",
            description = "soft delete item, and delete item from current cart")
    public void deleteItem(@PathVariable Long id) {
        shoppingCartService.deleteItem(id);
    }
}
