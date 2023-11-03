package org.store.structure.service.shoppingcart;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.store.structure.dto.cart_item.CartItemRequestDto;
import org.store.structure.dto.cart_item.CartItemUpdateDto;
import org.store.structure.dto.shopping_cart.ShoppingCartRequestDto;
import org.store.structure.dto.shopping_cart.ShoppingCartResponseDto;
import org.store.structure.mapper.CartItemMapper;
import org.store.structure.mapper.ShoppingCartMapper;
import org.store.structure.model.CartItem;
import org.store.structure.model.ShoppingCart;
import org.store.structure.repository.cart_item.CartItemRepository;
import org.store.structure.repository.shopping_cart.ShoppingCartRepository;

import java.util.Iterator;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final CartItemRepository cartItemRepository;
    private final CartItemMapper cartItemMapper;

    @Override
    public ShoppingCartResponseDto getCurrentCart() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        ShoppingCart shoppingCart = shoppingCartRepository.findAll().stream()
                .filter(cart -> cart.getUser().getEmail().equals(userName))
                .findFirst()
                .orElseThrow(EntityNotFoundException::new);
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    public ShoppingCartResponseDto save(ShoppingCartRequestDto requestDto) {
        ShoppingCart shoppingCart = shoppingCartMapper.toEntity(requestDto);
        return shoppingCartMapper.toDto(shoppingCartRepository.save(shoppingCart));
    }

    @Override
    @Transactional
    public ShoppingCartResponseDto updateItem(Long itemId, CartItemUpdateDto dto) {
        CartItem cartItem = cartItemRepository
                .findById(itemId)
                .orElseThrow(EntityNotFoundException::new);
        cartItem.setQuantity(dto.getQuantity());
        return getCurrentCart();
    }

    @Override
    @Transactional
    public ShoppingCartResponseDto addBook(CartItemRequestDto requestDto) {
        ShoppingCartResponseDto currentCartDto = getCurrentCart();
        ShoppingCart shoppingCart = shoppingCartRepository
                .findById(currentCartDto.getId())
                .orElseThrow(EntityNotFoundException::new);
        CartItem cartItem = cartItemMapper.toEntity(requestDto);
        shoppingCart.getCartItems().add(cartItem);
        return currentCartDto;
    }

    @Override
    @Transactional
    public ShoppingCartResponseDto deleteItem(Long itemId) {
        ShoppingCart shoppingCart = shoppingCartRepository
                .findById(getCurrentCart().getId())
                .orElseThrow(EntityNotFoundException::new);
        Iterator<CartItem> iterator = shoppingCart.getCartItems().stream().iterator();
        new CartItem();
        CartItem item;
        while (iterator.hasNext()) {
            item = iterator.next();
            if (item.getId().equals(itemId)) {
                iterator.remove();
                cartItemRepository.deleteById(itemId);
                break;
            }
        }
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    public void deleteById(Long id) {
        shoppingCartRepository.deleteById(id);
    }
}
