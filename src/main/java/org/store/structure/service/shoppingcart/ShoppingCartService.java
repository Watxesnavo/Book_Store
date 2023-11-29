package org.store.structure.service.shoppingcart;

import org.springframework.security.core.userdetails.UserDetails;
import org.store.structure.dto.cartitem.CartItemRequestDto;
import org.store.structure.dto.cartitem.CartItemUpdateDto;
import org.store.structure.dto.shoppingcart.ShoppingCartResponseDto;
import org.store.structure.model.ShoppingCart;

public interface ShoppingCartService {
    ShoppingCart getCurrentCart(UserDetails user);

    ShoppingCartResponseDto addBook(CartItemRequestDto requestDto, UserDetails user);

    ShoppingCartResponseDto updateItemQuantity(Long itemId, CartItemUpdateDto dto, UserDetails user);

    ShoppingCartResponseDto deleteItem(Long itemId, UserDetails user);

    void deleteById(Long id);
}
