package org.store.structure.service.cartitem;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.store.structure.dto.cartitem.CartItemRequestDto;
import org.store.structure.dto.cartitem.CartItemResponseDto;
import org.store.structure.exception.EntityNotFoundException;
import org.store.structure.mapper.CartItemMapper;
import org.store.structure.model.CartItem;
import org.store.structure.model.ShoppingCart;
import org.store.structure.repository.book.BookRepository;
import org.store.structure.repository.cartitem.CartItemRepository;
import org.store.structure.repository.shoppingcart.ShoppingCartRepository;

@RequiredArgsConstructor
@Service
public class CartItemServiceImpl implements CartItemService {
    private final CartItemRepository repository;
    private final CartItemMapper cartItemMapper;
    private final BookRepository bookRepository;
    private final ShoppingCartRepository shoppingCartRepository;

    @Override
    public List<CartItemResponseDto> findAll(Pageable pageable) {
        return repository.findAll(pageable).stream()
                .map(cartItemMapper::toDto)
                .toList();
    }

    @Override
    public CartItem getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't find cart by id:"
                        + id));
    }

    @Override
    public CartItemResponseDto save(CartItemRequestDto requestDto) {
        CartItem cartItem = new CartItem();
        cartItem.setBook(bookRepository.findById(requestDto.getBookId())
                .orElseThrow(() -> new EntityNotFoundException("Can't find book by id: "
                        + requestDto.getBookId())));
        cartItem.setQuantity(requestDto.getQuantity());
        cartItem.setShoppingCart(getCurrentCart());
        return cartItemMapper.toDto(repository.save(cartItem));
    }

    @Override
    @Transactional
    public CartItemResponseDto update(Long id, CartItemRequestDto requestDto) {
        CartItem cartItem = repository.findById(id).orElseThrow();
        cartItem.setBook(
                bookRepository
                        .findById(requestDto.getBookId())
                        .orElseThrow(() -> new EntityNotFoundException("Can't find book by id: "
                                + requestDto.getBookId())));
        cartItem.setQuantity(requestDto.getQuantity());
        return cartItemMapper.toDto(cartItem);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    private ShoppingCart getCurrentCart() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        return shoppingCartRepository.findFirstByUserEmail(userName)
                .orElseThrow(() -> new EntityNotFoundException("Can't find cart by email: "
                        + userName));
    }
}
