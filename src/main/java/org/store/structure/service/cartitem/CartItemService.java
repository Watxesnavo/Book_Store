package org.store.structure.service.cartitem;

import java.util.List;
import org.store.structure.dto.cartitem.CartItemRequestDto;
import org.store.structure.dto.cartitem.CartItemResponseDto;

public interface CartItemService {
    List<CartItemResponseDto> findAll();

    CartItemResponseDto getById(Long id);

    CartItemResponseDto save(CartItemRequestDto requestDto);

    CartItemResponseDto update(Long id, CartItemRequestDto requestDto);

    void deleteById(Long id);
}
