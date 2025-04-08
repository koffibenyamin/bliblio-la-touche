package org.latouche.repository;

import java.util.Optional;

import org.latouche.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;

import java.util.List;


public interface MemberRepository  extends JpaRepository<Member, Long>{
	
	Optional<Member> findByRegisterNumber(String registerNumber);
	
	void deleteByRegisterNumber(String registerNumber);


}
