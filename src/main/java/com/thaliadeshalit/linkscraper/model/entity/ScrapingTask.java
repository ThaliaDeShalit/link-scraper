package com.thaliadeshalit.linkscraper.model.entity;

import com.thaliadeshalit.linkscraper.model.enums.ScrapingStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class ScrapingTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;

    private ScrapingStatus status;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String result;
}
