package org.latouche.dto;

import lombok.Data;

@Data
public class BookDTO {
	private String title;
    private int numberOfCopies;
    private String description;
    private String genre;
    private String author;

}
