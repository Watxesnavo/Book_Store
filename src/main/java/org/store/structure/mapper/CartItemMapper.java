package org.store.structure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.store.structure.config.MapperConfig;
import org.store.structure.dto.cart_item.CartItemRequestDto;
import org.store.structure.dto.cart_item.CartItemResponseDto;
import org.store.structure.model.CartItem;
import org.store.structure.repository.book.BookRepository;

@Mapper(config = MapperConfig.class,
        uses = {BookRepository.class, BookMapper.class}
)
public interface CartItemMapper {
    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "book.title", target = "bookTitle")
    CartItemResponseDto toDto(CartItem cartItem);

    @Mapping(target = "book", source = "requestDto.bookId", qualifiedByName = "bookFromId")
    CartItem toEntity(CartItemRequestDto requestDto);
}
