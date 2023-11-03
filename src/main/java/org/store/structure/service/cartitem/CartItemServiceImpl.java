package org.store.structure.service.cartitem;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.store.structure.dto.cart_item.CartItemRequestDto;
import org.store.structure.dto.cart_item.CartItemResponseDto;
import org.store.structure.mapper.CartItemMapper;
import org.store.structure.model.CartItem;
import org.store.structure.repository.book.BookRepository;
import org.store.structure.repository.cart_item.CartItemRepository;

@RequiredArgsConstructor
@Service
public class CartItemServiceImpl implements CartItemService {
    private final CartItemRepository repository;
    private final CartItemMapper cartItemMapper;
    private final BookRepository bookRepository;

    @Override
    public List<CartItemResponseDto> findAll() {
        return repository.findAll().stream()
                .map(cartItemMapper::toDto)
                .toList();
    }

    @Override
    public CartItemResponseDto getById(Long id) {
        return cartItemMapper.toDto(repository.findById(id).orElseThrow(RuntimeException::new));
    }

    @Override
    public CartItemResponseDto save(CartItemRequestDto requestDto) {
        CartItem cartItem = cartItemMapper.toEntity(requestDto);
        return cartItemMapper.toDto(repository.save(cartItem));
    }

    @Override
    @Transactional
    public CartItemResponseDto update(Long id, CartItemRequestDto requestDto) {
        CartItem cartItem = repository.findById(id).orElseThrow(EntityNotFoundException::new);
        cartItem.setBook(
                bookRepository
                        .findById(requestDto.getBookId())
                        .orElseThrow(EntityNotFoundException::new));
        cartItem.setQuantity(requestDto.getQuantity());
        return cartItemMapper.toDto(cartItem);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
