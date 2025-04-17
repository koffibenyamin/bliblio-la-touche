package org.latouche.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.latouche.model.Book;
import org.latouche.model.Order;
import org.latouche.repository.BookRepository;
import org.latouche.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {

	private final OrderService orderService;
	private final BookRepository bookRepository;

	@GetMapping("/borrow")
    public String showAllBorrowOrders(Model model) {
        List<Order> orders = orderService.findAllOrders();
        List<Book> books = bookRepository.findAll();
        model.addAttribute("books", books);
        model.addAttribute("orders", orders);
        return "borrow"; // This refers to borrow.html
    }
	
	
	@PostMapping("/borrow")
	public String registerBorrowOrder(
	        @RequestParam String registerNumber,
	        @RequestParam List<String> serialNumbers,
	        @RequestParam Long period,
	        RedirectAttributes redirectAttributes
	) {
	    try {
	        // Filter out empty entries
	        List<String> filteredSerials = serialNumbers.stream()
	                .filter(s -> s != null && !s.trim().isEmpty())
	                .toList();

	        if (filteredSerials.isEmpty()) {
	            throw new RuntimeException("Veuillez entrer au moins un matricule de livre.");
	        }

	        orderService.registerOrder(registerNumber, filteredSerials, period);
	        redirectAttributes.addFlashAttribute("successMessage", "Emprunt enregistré avec succès !");
	    } catch (RuntimeException e) {
	        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
	    }

	    return "redirect:/order/borrow";
	}

	
    

	@GetMapping("/return")
    public String showReturnPage() {
        return "return";
    }

    @PostMapping("/return/search")
    public String findBorrowedBooksByMember(
            @RequestParam String registerNumber,
            Model model
    ) {
        Optional<Order> orderOpt = orderService.findActiveOrderByRegisterNumber(registerNumber);

        if (orderOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Aucun emprunt en cours trouvé pour ce matricule.");
            return "return";
        }

        Order order = orderOpt.get();
        model.addAttribute("order", order);
        model.addAttribute("borrowedBooks", order.getBooks());
        return "return";
    }

    @PostMapping("/return/submit")
    public String submitReturnedBooks(
            @RequestParam Long orderId,
            @RequestParam(name = "returnedSerials", required = false) List<String> returnedSerials,
            RedirectAttributes redirectAttributes
    ) {
        try {
            orderService.returnBooks(orderId, returnedSerials != null ? returnedSerials : new ArrayList<>());
            redirectAttributes.addFlashAttribute("successMessage", "Retour enregistré avec succès.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/order/return";
    }
    
    
    @GetMapping("/overdue/{memberId}")
    public ResponseEntity<Boolean> hasOverdueBooks(@PathVariable Long memberId) {
        return ResponseEntity.ok(orderService.hasOverdueBooks(memberId));
    }
}
