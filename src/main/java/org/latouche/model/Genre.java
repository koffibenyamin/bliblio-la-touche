package org.latouche.model;

import java.util.List;
import jakarta.persistence.Entity;
import lombok.Data;
import jakarta.persistence.*;


@Data
@Entity
public class Genre {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @OneToMany(mappedBy = "genre", cascade = CascadeType.ALL)
    private List<Book> books;

    public Genre() {}

    public Genre(String name) {
        this.name = name;
    }

}
