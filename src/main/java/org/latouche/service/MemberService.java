package org.latouche.service;

import java.util.List;
import java.util.Optional;

import org.latouche.model.Member;
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

    public Optional<Member> getMemberById(String id) {
        return memberRepository.findByRegisterNumber(id); 
    }

    public Member updateMember(String id, Member updatedMember) {
        return memberRepository.findByRegisterNumber(id).map(member -> {
        	member.setNamePerson(updatedMember.getNamePerson());
        	member.setFirstNamePerson(updatedMember.getFirstNamePerson());
        	member.setDateOfBirth(updatedMember.getDateOfBirth());
        	member.setPhoneNumber(updatedMember.getPhoneNumber());
        	member.setGender(updatedMember.getGender());
        	member.setEmail(updatedMember.getEmail());
            member.setStatus(updatedMember.getStatus());
            return memberRepository.save(member);
        }).orElseThrow(() -> new RuntimeException("Member not found"));
    }

    public void deleteMember(Long id) {
        memberRepository.deleteById(id);
    }

}
