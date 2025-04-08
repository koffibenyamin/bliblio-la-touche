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

    public Order returnBooks(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.isReturned()) {
            throw new RuntimeException("Books already returned");
        }

        order.setReturned(true);
        for (Book book : order.getBooks()) {
            book.returnBook(); // Increase available copies
        }

        bookRepository.saveAll(order.getBooks());
        return orderRepository.save(order);
    }

    public boolean hasOverdueBooks(Long memberId) {
        return orderRepository.findByMemberIdPersonAndReturnedFalse(memberId)
                .stream()
                .anyMatch(Order::isOverdue);
    }
    
    

}
