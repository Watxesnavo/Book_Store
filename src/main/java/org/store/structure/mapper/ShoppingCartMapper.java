package org.store.structure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.store.structure.config.MapperConfig;
import org.store.structure.dto.shopping_cart.ShoppingCartRequestDto;
import org.store.structure.dto.shopping_cart.ShoppingCartResponseDto;
import org.store.structure.model.ShoppingCart;
import org.store.structure.repository.user.UserRepository;
import org.store.structure.service.cartitem.CartItemService;

@Mapper(config = MapperConfig.class,
        imports = {CartItemService.class, UserRepository.class},
        uses = CartItemMapper.class
)
public interface ShoppingCartMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "cartItems", target = "cartItems")
    ShoppingCartResponseDto toDto(ShoppingCart shoppingCart);

    @Mapping(target = "user", expression = "java(" +
            "userRepository.findById(requestDto.getUserId()).orElseThrow()" +
            ")")
    @Mapping(source = "cartItems", target = "cartItems")
    ShoppingCart toEntity(ShoppingCartRequestDto requestDto);
}
