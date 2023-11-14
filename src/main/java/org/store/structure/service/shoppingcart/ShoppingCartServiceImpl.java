package org.store.structure.service.shoppingcart;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.store.structure.dto.cartitem.CartItemRequestDto;
import org.store.structure.dto.cartitem.CartItemResponseDto;
import org.store.structure.dto.cartitem.CartItemUpdateDto;
import org.store.structure.dto.shoppingcart.ShoppingCartResponseDto;
import org.store.structure.exception.EntityNotFoundException;
import org.store.structure.mapper.ShoppingCartMapper;
import org.store.structure.model.CartItem;
import org.store.structure.model.ShoppingCart;
import org.store.structure.repository.shoppingcart.ShoppingCartRepository;
import org.store.structure.service.cartitem.CartItemService;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final CartItemService cartItemService;

    @Override
    public ShoppingCart getCurrentCart(UserDetails user) {
        log.info("started getCurrentCart method now");
        return findFirstByEmail(user.getUsername());
    }

    @Override
    @Transactional
    public ShoppingCartResponseDto updateItem(
            Long itemId, CartItemUpdateDto dto, UserDetails user
    ) {
        CartItem cartItem = cartItemService.getById(itemId);
        cartItem.setQuantity(dto.getQuantity());
        return shoppingCartMapper.toDto(getCurrentCart(user));
    }

    @Override
    @Transactional
    public ShoppingCartResponseDto addBook(CartItemRequestDto requestDto, UserDetails user) {
        log.info("started addBook method now");
        CartItemResponseDto saved = cartItemService.save(requestDto, user);
        ShoppingCart currentCart = getCurrentCart(user);
        CartItem cartItem = cartItemService.getById(saved.getId());
        currentCart.getCartItems().add(cartItem);
        return shoppingCartMapper.toDto(currentCart);
    }

    @Override
    @Transactional
    public ShoppingCartResponseDto deleteItem(Long itemId, UserDetails user) {
        cartItemService.deleteById(itemId);
        return shoppingCartMapper.toDto(getCurrentCart(user));
    }

    @Override
    public void deleteById(Long id) {
        shoppingCartRepository.deleteById(id);
    }

    @Override
    public ShoppingCart findFirstByEmail(String email) {
        return shoppingCartRepository.findFirstByUserEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Can't find a cart by email: "
                        + email));
    }
}
