package org.latouche.service;

import java.util.List;
import java.util.Optional;

import org.latouche.model.Member;
import org.latouche.model.Status;
import org.latouche.repository.MemberRepository;
import org.springframework.stereotype.Service;

@Service
public class MemberService {
	
	private final MemberRepository memberRepository;

	public MemberService(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}
	
	public Member createMember(Member member) {
        return memberRepository.save(member);
    }

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Optional<Member> getMemberById(Long id) {
        return memberRepository.findById(id); 
    }

    public Member updateMember(Long id, Member updatedMember) {
        return memberRepository.findById(id).map(member -> {
        	member.setNamePerson(updatedMember.getNamePerson());
        	member.setFirstNamePerson(updatedMember.getFirstNamePerson());
        	member.setDateOfBirth(updatedMember.getDateOfBirth());
        	member.setPhoneNumber(updatedMember.getPhoneNumber());
        	member.setGender(updatedMember.getGender());
        	member.setEmail(updatedMember.getEmail());
            
            return memberRepository.save(member);
        }).orElseThrow(() -> new RuntimeException("Member not found"));
    }
    
    public void toggleStatus(Long id) {
        Member member = memberRepository.findById(id).orElseThrow();
        if (member.getStatus() == Status.ACTIVE) {
            member.setStatus(Status.BLOCKED);
        } else {
            member.setStatus(Status.ACTIVE);
        }
        memberRepository.save(member);
    }

    public void deleteMember(Long id) {
        memberRepository.deleteById(id);
    }

}
