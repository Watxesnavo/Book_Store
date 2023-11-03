package org.store.structure.service.shoppingcart;

import org.store.structure.dto.cart_item.CartItemRequestDto;
import org.store.structure.dto.cart_item.CartItemUpdateDto;
import org.store.structure.dto.shopping_cart.ShoppingCartRequestDto;
import org.store.structure.dto.shopping_cart.ShoppingCartResponseDto;

public interface ShoppingCartService {
    ShoppingCartResponseDto getCurrentCart();

    ShoppingCartResponseDto addBook(CartItemRequestDto requestDto);

    ShoppingCartResponseDto save(ShoppingCartRequestDto requestDto);

    ShoppingCartResponseDto updateItem(Long itemId, CartItemUpdateDto dto);

    ShoppingCartResponseDto deleteItem(Long itemId);

    void deleteById(Long id);
}
