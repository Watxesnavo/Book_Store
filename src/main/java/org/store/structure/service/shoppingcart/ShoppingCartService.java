package org.store.structure.service.shoppingcart;

import org.store.structure.dto.cartitem.CartItemUpdateDto;
import org.store.structure.dto.shoppingcart.ShoppingCartResponseDto;
import org.store.structure.model.ShoppingCart;

public interface ShoppingCartService {
    ShoppingCart getCurrentCart();

    ShoppingCartResponseDto addBook(Long itemId);

    ShoppingCartResponseDto updateItem(Long itemId, CartItemUpdateDto dto);

    ShoppingCartResponseDto deleteItem(Long itemId);

    void deleteById(Long id);
}
