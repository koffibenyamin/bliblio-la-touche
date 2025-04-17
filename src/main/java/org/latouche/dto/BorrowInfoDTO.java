package org.latouche.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class BorrowInfoDTO {
	private Long memberId;
    private String fullName;
    private String registerNumber;
    private String phoneNumber;
    private String email;
    private LocalDate borrowDate;
    private LocalDate returnDate;
    private boolean returned;
}
