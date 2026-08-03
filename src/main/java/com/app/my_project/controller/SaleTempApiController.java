package com.app.my_project.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.my_project.entity.BillSaleDetailEntity;
import com.app.my_project.entity.BillSaleEntity;
import com.app.my_project.entity.SaleTempEntity;
import com.app.my_project.entity.UserEntity;
import com.app.my_project.jpa.EndSaleJpa;
import com.app.my_project.repository.BillSaleDetailRepository;
import com.app.my_project.repository.BillSaleRepository;
import com.app.my_project.repository.ProductionRepository;
import com.app.my_project.repository.SaleTempRepository;
import com.app.my_project.repository.UserRepository;
import com.app.my_project.service.SaleTempService;
import com.app.my_project.service.UserService;

/**
 * SaleTempApiController
 * REST API controller for managing temporary sale cart operations.
 */
@RestController
@RequestMapping("/api/SaleTemp")
public class SaleTempApiController {
    @Autowired
    private SaleTempRepository saleTempRepository; // For list, delete, update, endSale

    @Autowired
    private ProductionRepository productionRepository; // For endSale

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BillSaleRepository billSaleRepository;

    @Autowired
    private BillSaleDetailRepository billSaleDetailRepository;

    @Autowired
    private SaleTempService saleTempService; // Service layer for business logic

    @Autowired
    private UserService userService;

    /**
     * Add a product to the temporary sale cart.
     * Uses the service layer for business logic.
     */
    @PostMapping
    public SaleTempEntity create(
            @RequestBody SaleTempEntity saleTemp,
            @RequestHeader("Authorization") String token) {
        Long userId = userService.getUserIdFromToken(token);
        Long productionId = saleTemp.getProduction().getId();

        // Use the service layer instead of direct business logic here
        return saleTempService.addProductToSaleTemp(productionId, userId);
    }

    /**
     * List all temporary sale cart items for the current user.
     */
    @GetMapping
    public List<SaleTempEntity> list(@RequestHeader("Authorization") String token) {
        Long userId = userService.getUserIdFromToken(token);
        return saleTempRepository.findAllByUserIdOrderByIdDesc(userId);
    }

    /**
     * Delete a temporary sale cart item by ID.
     */
    @DeleteMapping("/{id}")
    public void delete(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        Long userId = userService.getUserIdFromToken(token);

        if (userId == null) {
            throw new RuntimeException("user not found");
        }

        saleTempRepository.deleteById(id);
    }

    /**
     * Update the quantity of a temporary sale cart item.
     */
    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @RequestBody SaleTempEntity saleTemp) {
        SaleTempEntity saleTempEntity = saleTempRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("sale temp not found"));

        saleTempEntity.setQty(saleTemp.getQty());
        saleTempRepository.save(saleTempEntity);
    }

    /**
     * Complete the sale: create BillSale and BillSaleDetail records, then clear the
     * temp cart.
     */
    // @Transactional: the bill header, every line item and the cart cleanup must
    // land together - a failure halfway through would leave a bill with missing
    // line items, which is financial data that cannot be reconstructed
    @Transactional
    @PostMapping("/endSale")
    public void endSale(
            @RequestHeader("Authorization") String token,
            @RequestBody EndSaleJpa billSale) {
        Long userId = userService.getUserIdFromToken(token);

        if (userId == null) {
            throw new RuntimeException("user not found");
        }

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("user not found"));

        List<SaleTempEntity> saleTemps = saleTempRepository.findAllByUserIdOrderByIdDesc(userId);

        if (saleTemps.isEmpty()) {
            throw new IllegalStateException("Cannot close a sale with an empty cart");
        }

        // The total is derived from the cart, never taken from the request body:
        // a client that posts the wrong total would otherwise save a bill whose
        // header disagrees with its own line items, and revenue reports sum the
        // line items rather than this field.
        double total = 0;
        for (SaleTempEntity saleTemp : saleTemps) {
            total += saleTemp.getPrice() * saleTemp.getQty();
        }

        BillSaleEntity billSaleEntity = new BillSaleEntity();
        billSaleEntity.setInputMoney(billSale.getInputMoney());
        billSaleEntity.setDiscount(billSale.getDiscount());
        billSaleEntity.setTotal(total);
        billSaleEntity.setStatus("paid");
        billSaleEntity.setCreatedAt(LocalDate.now());
        billSaleEntity.setUser(userEntity);

        billSaleRepository.save(billSaleEntity);

        for (SaleTempEntity saleTemp : saleTemps) {
            BillSaleDetailEntity billSaleDetailEntity = new BillSaleDetailEntity();
            billSaleDetailEntity.setBillSale(billSaleEntity);
            billSaleDetailEntity.setProduction(saleTemp.getProduction());
            billSaleDetailEntity.setQuantity(saleTemp.getQty());
            billSaleDetailEntity.setPrice(saleTemp.getPrice());
            billSaleDetailRepository.save(billSaleDetailEntity);

            saleTempRepository.delete(saleTemp);
        }
    }
}