package org.latouche.controller;

import java.util.List;

import org.latouche.model.Order;
import org.latouche.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/order")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class OrderController {

	private final OrderService orderService;

    @PostMapping("/borrow")
    public ResponseEntity<Order> borrowBooks(
            @RequestParam Long memberId, 
            @RequestParam List<Long> bookIds, 
            @RequestParam Long period) {
        return ResponseEntity.ok(orderService.borrowBooks(memberId, bookIds, period));
    }

    @PostMapping("/return/{orderId}")
    public ResponseEntity<Order> returnBooks(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.returnBooks(orderId));
    }

    @GetMapping("/overdue/{memberId}")
    public ResponseEntity<Boolean> hasOverdueBooks(@PathVariable Long memberId) {
        return ResponseEntity.ok(orderService.hasOverdueBooks(memberId));
    }
}
