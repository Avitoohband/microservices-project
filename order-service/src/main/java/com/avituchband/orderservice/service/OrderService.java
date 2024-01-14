package com.avituchband.orderservice.service;

import com.avituchband.orderservice.dto.InventoryResponse;
import com.avituchband.orderservice.dto.OrderLineItemsDto;
import com.avituchband.orderservice.dto.OrderRequest;
import com.avituchband.orderservice.model.Order;
import com.avituchband.orderservice.model.OrderLineItems;
import com.avituchband.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;

    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItemsList = orderRequest.getOrderLineItemsListDto()
                .stream()
                .map(this::toOrderLineItems)
                .toList();

        order.setOrderLineItemsList(orderLineItemsList);

        if (Boolean.TRUE.equals(getIsInStock(order))) {
            orderRepository.save(order);
        } else {
            throw new IllegalArgumentException("Product is not in stock, please try again later.");
        }
    }

    private Boolean getIsInStock(Order order) {


        String skuCodeParams = order.getOrderLineItemsList()
                .stream()
                .map(orderLineItems -> "skuCodeList=" + orderLineItems.getSkuCode()).
        collect(Collectors.joining("&"));

        InventoryResponse[] inventoryResponseArray = webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory?" + skuCodeParams)
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        // Call Inventory Service, and place order in product is in stock
        if (inventoryResponseArray != null) {
            return Arrays.stream(inventoryResponseArray)
                    .allMatch(InventoryResponse::getIsInStock);
        } else {
            return false;
        }
    }

    public OrderLineItems toOrderLineItems(OrderLineItemsDto orderLineItemsDto) {
        return new OrderLineItems(
                orderLineItemsDto.getId(),
                orderLineItemsDto.getSkuCode(),
                orderLineItemsDto.getPrice(),
                orderLineItemsDto.getQuantity()
        );
    }
}
