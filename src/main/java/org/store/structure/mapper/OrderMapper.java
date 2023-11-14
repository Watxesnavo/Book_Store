package org.store.structure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.store.structure.config.MapperConfig;
import org.store.structure.dto.order.OrderResponseDto;
import org.store.structure.model.Order;

@Mapper(
        config = MapperConfig.class,
        uses = {OrderItemMapper.class}
)
public interface OrderMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "orderItems", source = "orderItems")
    OrderResponseDto toDto(Order order);
}
