package com.avituchband.inventoryservice;

import com.avituchband.inventoryservice.model.Inventory;
import com.avituchband.inventoryservice.repository.InventoryRepository;
import com.avituchband.inventoryservice.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

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


    @Test
    void shouldReturnExists() throws Exception {

        String skuCode = "iphone_13_Red";

        mockMvc.perform(MockMvcRequestBuilders.get("/api/inventory/" + skuCode)
        ).andExpect(status().isOk());

        when(inventoryRepository.findFirstBySkuCode(any())).thenReturn(Optional.of(
                Inventory.builder().skuCode(skuCode).build()
        ));

        assertTrue(inventoryService.isInStock(skuCode));


    }

}
