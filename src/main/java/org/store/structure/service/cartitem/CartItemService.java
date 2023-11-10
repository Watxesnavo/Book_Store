package org.store.structure.service.cartitem;

import java.util.List;
import org.store.structure.dto.cartitem.CartItemRequestDto;
import org.store.structure.dto.cartitem.CartItemResponseDto;
import org.store.structure.model.CartItem;

public interface CartItemService {
    List<CartItemResponseDto> findAll();

    CartItem getById(Long id);

    CartItemResponseDto save(CartItemRequestDto requestDto);

    CartItemResponseDto update(Long id, CartItemRequestDto requestDto);

    void deleteById(Long id);
}
