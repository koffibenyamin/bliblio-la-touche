package org.latouche.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookDTO {
	
	private Long id;
    private String serialNumber;
    private String title;
    private int numberOfCopies;
    private String description;
    private String thumbnailPath;
    private String genreName;
    private String authorName;
    private int availableCopies;
    
	public BookDTO() {
		
	}

	public BookDTO(String serialNumber, String title, String thumbnailPath) {
		this.serialNumber = serialNumber;
		this.title = title;
		this.thumbnailPath = thumbnailPath;
	}
	
	
    
    

}
