package lk.ijse.eca.orderservice.mapper;

import lk.ijse.eca.orderservice.dto.OrderDto;
import lk.ijse.eca.orderservice.entity.Order;
import lk.ijse.eca.orderservice.entity.OrderItem;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface OrderMapper {

    OrderDto toDto(Order order);

    Order toEntity(OrderDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "orderId", ignore = true)
    void updateEntity(OrderDto dto, @MappingTarget Order order);

    @Mapping(target = "orderItems", source = "orderItems", qualifiedByName = "mapOrderItemEntitiesToDtos")
    OrderDto toDtoWithItems(Order order);

    @Mapping(target = "orderItems", source = "orderItems", qualifiedByName = "mapOrderItemDtosToEntities")
    Order toEntityWithItems(OrderDto dto);

    @Named("mapOrderItemEntitiesToDtos")
    default List<OrderDto.OrderItemDto> mapOrderItemEntitiesToDtos(List<OrderItem> orderItems) {
        if (orderItems == null) return null;
        return orderItems.stream()
                .map(this::mapOrderItemToDto)
                .toList();
    }

    @Named("mapOrderItemDtosToEntities")
    default List<OrderItem> mapOrderItemDtosToEntities(List<OrderDto.OrderItemDto> orderItemDtos) {
        if (orderItemDtos == null) return null;
        return orderItemDtos.stream()
                .map(this::mapOrderItemDtoToEntity)
                .toList();
    }

    default OrderDto.OrderItemDto mapOrderItemToDto(OrderItem orderItem) {
        if (orderItem == null) return null;
        return OrderDto.OrderItemDto.builder()
                .orderItemId(orderItem.getOrderItemId())
                .productId(orderItem.getProductId())
                .productName(orderItem.getProductName())
                .quantity(orderItem.getQuantity())
                .unitPrice(orderItem.getUnitPrice())
                .totalPrice(orderItem.getTotalPrice())
                .discountAmount(orderItem.getDiscountAmount())
                .build();
    }

    default OrderItem mapOrderItemDtoToEntity(OrderDto.OrderItemDto dto) {
        if (dto == null) return null;
        return new OrderItem(
                dto.getOrderItemId(),
                null, // order will be set separately
                dto.getProductId(),
                dto.getProductName(),
                dto.getQuantity(),
                dto.getUnitPrice(),
                dto.getTotalPrice(),
                dto.getDiscountAmount()
        );
    }
}
