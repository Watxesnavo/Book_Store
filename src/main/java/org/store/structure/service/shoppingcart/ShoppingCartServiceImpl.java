package org.store.structure.service.shoppingcart;

import java.util.Iterator;
import lombok.RequiredArgsConstructor;
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
import org.store.structure.repository.cartitem.CartItemRepository;
import org.store.structure.repository.shoppingcart.ShoppingCartRepository;
import org.store.structure.service.cartitem.CartItemService;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final CartItemRepository cartItemRepository;
    private final CartItemService cartItemService;

    @Override
    public ShoppingCart getCurrentCart() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        return shoppingCartRepository.findAll().stream()
                .filter(cart -> cart.getUser().getEmail().equals(userName))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Can't find a cart by email: "
                        + userName));
    }

    @Override
    @Transactional
    public ShoppingCartResponseDto updateItem(Long itemId, CartItemUpdateDto dto) {
        CartItem cartItem = cartItemRepository
                .findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find item by id: " + itemId));
        cartItem.setQuantity(dto.getQuantity());
        return shoppingCartMapper.toDto(getCurrentCart());
    }

    @Override
    public ShoppingCartResponseDto addBook(CartItemRequestDto requestDto) {
        CartItemResponseDto savedItem = cartItemService.save(requestDto);
        ShoppingCart currentCart = getCurrentCart();
        CartItem cartItem = cartItemRepository.findById(savedItem.getId())
                .orElseThrow(() -> new EntityNotFoundException("Can't find item by id: "
                        + savedItem.getId()));
        currentCart.getCartItems().add(cartItem);
        return shoppingCartMapper.toDto(currentCart);
    }

    @Override
    @Transactional
    public ShoppingCartResponseDto deleteItem(Long itemId) {
        ShoppingCart shoppingCart = shoppingCartRepository
                .findById(getCurrentCart().getId())
                .orElseThrow(() -> new EntityNotFoundException("Can't find a cart by id: "
                        + getCurrentCart().getId()));
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
