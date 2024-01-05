package org.store.structure.service.shoppingcart;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.store.structure.dto.cartitem.CartItemRequestDto;
import org.store.structure.dto.cartitem.CartItemUpdateDto;
import org.store.structure.dto.shoppingcart.ShoppingCartResponseDto;
import org.store.structure.exception.EntityNotFoundException;
import org.store.structure.mapper.ShoppingCartMapper;
import org.store.structure.model.CartItem;
import org.store.structure.model.ShoppingCart;
import org.store.structure.model.User;
import org.store.structure.repository.book.BookRepository;
import org.store.structure.repository.cartitem.CartItemRepository;
import org.store.structure.repository.shoppingcart.ShoppingCartRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final BookRepository bookRepository;
    private final CartItemRepository cartItemRepository;
    private final ShoppingCartMapper shoppingCartMapper;

    @Override
    public ShoppingCart getCurrentCart(User user) {
        return shoppingCartRepository.findFirstByUserEmail(user.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Can't find a cart by email: "
                        + user.getUsername()));
    }

    @Override
    @Transactional
    public ShoppingCartResponseDto updateItemQuantity(
            Long itemId, CartItemUpdateDto dto, User user
    ) {
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find item by id: "
                        + itemId));
        cartItem.setQuantity(dto.getQuantity());
        return shoppingCartMapper.toDto(getCurrentCart(user));
    }

    @Override
    @Transactional
    public ShoppingCartResponseDto addBook(CartItemRequestDto requestDto, User user) {
        ShoppingCart currentCart = getCurrentCart(user);
        Optional<CartItem> currItemOptional =
                cartItemRepository.findByBookId(requestDto.getBookId());
        if (currItemOptional.isPresent()) {
            currItemOptional.get().setQuantity(requestDto.getQuantity());
            return shoppingCartMapper.toDto(currentCart);
        }
        CartItem cartItem = new CartItem();
        cartItem.setBook(bookRepository.findById(requestDto.getBookId())
                .orElseThrow(() -> new EntityNotFoundException("Can't find book by id: "
                        + requestDto.getBookId())));
        cartItem.setQuantity(requestDto.getQuantity());
        cartItem.setShoppingCart(currentCart);
        currentCart.getCartItems().add(cartItem);
        return shoppingCartMapper.toDto(currentCart);
    }

    @Override
    @Transactional
    public ShoppingCartResponseDto deleteItem(Long itemId, User user) {
        cartItemRepository.deleteById(itemId);
        return shoppingCartMapper.toDto(getCurrentCart(user));
    }

    @Override
    public void deleteById(Long id) {
        shoppingCartRepository.deleteById(id);
    }
}
