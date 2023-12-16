package org.store.structure.service.shoppingcart;

import org.springframework.security.core.userdetails.UserDetails;
import org.store.structure.dto.cartitem.CartItemRequestDto;
import org.store.structure.dto.cartitem.CartItemUpdateDto;
import org.store.structure.dto.shoppingcart.ShoppingCartResponseDto;
import org.store.structure.model.ShoppingCart;
import org.store.structure.model.User;

public interface ShoppingCartService {
    ShoppingCart getCurrentCart(User user);

    ShoppingCartResponseDto addBook(CartItemRequestDto requestDto, User user);

    ShoppingCartResponseDto updateItemQuantity(Long itemId, CartItemUpdateDto dto, User user);

    ShoppingCartResponseDto deleteItem(Long itemId, User user);

    void deleteById(Long id);
}
