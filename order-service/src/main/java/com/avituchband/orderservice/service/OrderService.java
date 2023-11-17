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

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient webClient;

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
        List<String> skuCodeList = order.getOrderLineItemsList()
                .stream()
                .map(OrderLineItems::getSkuCode)
                .toList();

        InventoryResponse[] inventoryResponseArray = webClient.get()
                .uri("http://localhost:8082/api/inventory/",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodeList).build())
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
