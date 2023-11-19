package com.avituchband.inventoryservice;

import com.avituchband.inventoryservice.dto.InventoryResponse;
import com.avituchband.inventoryservice.model.Inventory;
import com.avituchband.inventoryservice.repository.InventoryRepository;
import com.avituchband.inventoryservice.service.InventoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class InventoryServiceApplicationTests {

    @Autowired
    MockMvc mockMvc;
    @InjectMocks
    InventoryService inventoryService;
    @Mock
    InventoryRepository inventoryRepository;
    @Autowired
    ObjectMapper objectMapper;


    @Test
    void shouldReturnExists() throws Exception {
        List<String> skuCodeList = new ArrayList<>();
        skuCodeList.add("iphone_13");
        skuCodeList.add("iphone_13_red");

        String skuCodeListParams = skuCodeList.stream()
                .map(skuCode -> "skuCodeList=" + skuCode).collect(Collectors.joining("&"));

        InventoryResponse expectedInventoryResponse = new InventoryResponse();
        expectedInventoryResponse.setSkuCode("Iphone_13");
        expectedInventoryResponse.setIsInStock(true);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/inventory?" + skuCodeListParams).
                contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        when(inventoryRepository.findFirstBySkuCodeIn(any())).thenReturn(getInventories());

        List<InventoryResponse> serviceResponseList = inventoryService.isInStock(skuCodeList);
        InventoryResponse serviceResponse = serviceResponseList.get(0);

        assertEquals(expectedInventoryResponse.getSkuCode(), serviceResponse.getSkuCode());
        assertEquals(expectedInventoryResponse.getIsInStock(), serviceResponse.getIsInStock());

    }

    private static List<Inventory> getInventories() {
        List<Inventory> inventoryList = new ArrayList<>();

        Inventory inventory = new Inventory();
        inventory.setSkuCode("Iphone_13");
        inventory.setQuantity(100);

        Inventory inventory2 = new Inventory();
        inventory2.setSkuCode("Iphone_13_red");
        inventory2.setQuantity(0);

        inventoryList.add(inventory);
        inventoryList.add(inventory2);
        return inventoryList;
    }

}
