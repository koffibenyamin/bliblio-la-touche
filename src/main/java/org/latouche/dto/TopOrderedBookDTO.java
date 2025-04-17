package org.latouche.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TopOrderedBookDTO {
	
	private Long id;
    private String title;
    private String thumbnailPath;

}
