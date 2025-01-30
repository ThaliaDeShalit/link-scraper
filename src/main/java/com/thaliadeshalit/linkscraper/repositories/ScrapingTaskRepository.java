package com.thaliadeshalit.linkscraper.repositories;

import com.thaliadeshalit.linkscraper.model.entity.ScrapingTask;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ScrapingTaskRepository extends JpaRepository<ScrapingTask, Long> {
    Optional<ScrapingTask> findByUrl(String url);
}

