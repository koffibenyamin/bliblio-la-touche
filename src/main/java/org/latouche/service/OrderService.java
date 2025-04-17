package org.latouche.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.latouche.model.Book;
import org.latouche.model.Member;
import org.latouche.model.Order;
import org.latouche.model.User;
import org.latouche.repository.BookRepository;
import org.latouche.repository.MemberRepository;
import org.latouche.repository.OrderRepository;
import org.latouche.repository.UserRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
	
    
    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;
  
    
    public List<Order> findAllOrders() {
        return orderRepository.findAll();
    }

    
    public boolean isBookAvailable(Long bookId) {
        Optional<Book> bookOpt = bookRepository.findById(bookId);
        if (bookOpt.isEmpty()) {
            return false;
        }

        Book book = bookOpt.get();
        int borrowedCopies = orderRepository.countBorrowedCopies(bookId);
        return (book.getNumberOfCopies() - borrowedCopies) > 0;
    }
    

    public Order borrowBooks(Long memberId, List<Long> bookIds, Long period) {
    	
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        List<Book> books = bookRepository.findAllById(bookIds);
        for (Book book : books) {
            if (!book.isAvailable()) {
                throw new RuntimeException("Book " + book.getSerialNumber() + " is not available");
            }
        }

        Order order = new Order();
        order.setMember(member);
        order.setBooks(books);
        order.setBorrowDate(LocalDate.now());
        order.setReturnDate(LocalDate.now().plusDays(period)); // Borrowing period: 2 weeks


        for (Book book : books) {
            book.borrowBook(); // Reduce available copies
        }

        bookRepository.saveAll(books);
        return orderRepository.save(order);
    }
    
///////////////
///

    public String tryBorrowBooks(String registerNumber, List<String> serialNumbers, Long period) {

        Member member = memberRepository.findByRegisterNumber(registerNumber)
                .orElseThrow(() -> new RuntimeException("Membre introuvable."));

        // Check for active orders
        boolean hasActiveOrder = member.getOrders().stream()
                .anyMatch(order -> !order.isReturned());
        if (hasActiveOrder) {
            return "Ce membre a déjà un emprunt non retourné.";
        }

        List<Book> books = bookRepository.findBySerialNumberIn(serialNumbers);

        for (Book book : books) {
            if (!book.isAvailable()) {
                return "Le livre " + book.getSerialNumber() + " n'est pas disponible.";
            }
        }

        Order order = new Order();
        order.setMember(member);
        order.setBooks(books);
        order.setBorrowDate(LocalDate.now());
        order.setReturnDate(LocalDate.now().plusDays(period));

        for (Book book : books) {
            book.borrowBook(); // decrease stock
        }

        bookRepository.saveAll(books);
        orderRepository.save(order);

        return "SUCCESS";
    }
///
///

    public void registerOrder(String registerNumber, List<String> serialNumbers, Long period) {
        Member member = memberRepository.findByRegisterNumber(registerNumber)
            .orElseThrow(() -> new RuntimeException("Membre introuvable."));

        if (orderRepository.existsByMemberAndReturnedFalse(member)) {
            throw new RuntimeException("Ce membre a déjà un emprunt en cours.");
        }

        List<Book> books = bookRepository.findBySerialNumberIn(serialNumbers);
        if (books.size() < serialNumbers.size()) {
            throw new RuntimeException("Un ou plusieurs livres sont introuvables.");
        }

        for (Book book : books) {
            if (!book.isAvailable()) {
                throw new RuntimeException("Le livre " + book.getSerialNumber() + " n'est pas disponible.");
            }
            book.borrowBook();
        }

        Order order = new Order();
        order.setMember(member);
        order.setBooks(books);
        order.setBorrowDate(LocalDate.now());
        order.setReturnDate(LocalDate.now().plusDays(period));
        orderRepository.save(order);
        bookRepository.saveAll(books);
    }

///

    
    
    
    

    public Order returnBooks(Long orderId, List<String> returnedSerials) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.isReturned()) {
            throw new RuntimeException("Books already returned.");
        }

        List<Book> books = order.getBooks();
        for (Book book : books) {
            if (returnedSerials.contains(book.getSerialNumber())) {
                book.returnBook(); // increment copies
            }
        }

        if (books.stream().allMatch(book -> returnedSerials.contains(book.getSerialNumber()))) {
            order.setReturned(true);
        }

        bookRepository.saveAll(books);
        return orderRepository.save(order);
    }
    
    
    public Optional<Order> findActiveOrderByRegisterNumber(String registerNumber) {
        Member member = memberRepository.findByRegisterNumber(registerNumber)
                .orElseThrow(() -> new RuntimeException("Membre introuvable avec le matricule: " + registerNumber));

        return orderRepository.findByMemberAndReturnedFalse(member);
    }



    public boolean hasOverdueBooks(Long memberId) {
        return orderRepository.findByMemberIdPersonAndReturnedFalse(memberId)
                .stream()
                .anyMatch(Order::isOverdue);
    }
    
    

}
