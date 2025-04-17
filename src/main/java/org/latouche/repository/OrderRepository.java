package org.latouche.repository;

import java.util.List;
import java.util.Optional;

import org.latouche.model.Member;
import org.latouche.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository  extends JpaRepository<Order, Long> {

	List<Order> findByMemberIdPersonAndReturnedFalse(Long memberId); 
	
	@Query("""
	        SELECT COUNT(o) FROM Order o 
	        JOIN o.books b 
	        WHERE b.id = :bookId AND o.returned = false
	    """)
	    int countBorrowedCopies(@Param("bookId") Long bookId);
	
	Optional<Order> findByMemberAndReturnedFalse(Member member);
	


	
	@Query("SELECT o FROM Order o JOIN FETCH o.books WHERE o.member.idPerson = :memberId")
    List<Order> findOrdersByMemberId(@Param("memberId") Long memberId);
	
	@Query("SELECT o.member.idPerson, o.member.namePerson, o.member.firstNamePerson, o.member.registerNumber, " +
		       "o.member.phoneNumber, o.member.email, o.borrowDate, o.returnDate, o.returned " +
		       "FROM Order o JOIN o.books b WHERE b.id = :bookId")
		List<Object[]> findBorrowInfoByBookId(@Param("bookId") Long bookId);
		
		
		@Query("SELECT o FROM Order o JOIN o.member m WHERE m.registerNumber = :registerNumber AND o.returned = false")
		Optional<Order> findActiveOrderByRegisterNumber(@Param("registerNumber") String registerNumber);


	boolean existsByMemberAndReturnedFalse(Member member);
}
