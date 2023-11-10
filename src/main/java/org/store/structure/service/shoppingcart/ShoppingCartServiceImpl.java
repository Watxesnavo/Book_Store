package org.store.structure.service.shoppingcart;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public ShoppingCart getCurrentCart() {
        log.info("started getCurrentCart method now");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        return findFirstByEmail(userName);
    }

    @Override
    @Transactional
    public ShoppingCartResponseDto updateItem(Long itemId, CartItemUpdateDto dto) {
        CartItem cartItem = cartItemService.getById(itemId);
        cartItem.setQuantity(dto.getQuantity());
        return shoppingCartMapper.toDto(getCurrentCart());
    }

    @Override
    public ShoppingCartResponseDto addBook(CartItemRequestDto requestDto) {
        log.info("started addBook method now");
        CartItemResponseDto saved = cartItemService.save(requestDto);
        ShoppingCart currentCart = getCurrentCart();
        CartItem cartItem = cartItemService.getById(saved.getId());
        currentCart.getCartItems().add(cartItem);
        return shoppingCartMapper.toDto(currentCart);
    }

    @Override
    @Transactional
    public ShoppingCartResponseDto deleteItem(Long itemId) {
        cartItemService.deleteById(itemId);
        return shoppingCartMapper.toDto(getCurrentCart());
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
