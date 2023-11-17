package com.avituchband.inventoryservice.repository;


import com.avituchband.inventoryservice.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    List<Inventory> findFirstBySkuCodeIn(List<String> skuCode);



}
