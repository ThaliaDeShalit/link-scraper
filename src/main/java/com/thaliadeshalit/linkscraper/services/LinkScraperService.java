package com.thaliadeshalit.linkscraper.services;

import com.thaliadeshalit.linkscraper.model.dto.ScrapeResponseDto;
import com.thaliadeshalit.linkscraper.model.entity.ScrapingTask;
import com.thaliadeshalit.linkscraper.model.enums.ScrapingStatus;
import com.thaliadeshalit.linkscraper.repositories.ScrapingTaskRepository;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

@Service
public class LinkScraperService {

    private final ScrapingTaskRepository repository;
    private final ScraperWorkerService scraperWorkerService;
    private final ObjectMapper objectMapper;

    public LinkScraperService(ScrapingTaskRepository repository,
                              ObjectMapper objectMapper,
                              ScraperWorkerService scraperWorkerService) {
        this.repository = repository;
        this.scraperWorkerService = scraperWorkerService;
        this.objectMapper = objectMapper;
    }

    /**
     * Checks if a URL is already being scraped, and if not, starts a new task.
     * @param url The URL to scrape.
     * @return Task ID.
     */
    public Long initiateScraping(String url) {
        Optional<ScrapingTask> existingTask = repository.findByUrl(url);
        if (existingTask.isPresent()) {
            return existingTask.get().getId();
        }

        ScrapingTask task = ScrapingTask.builder()
                .url(url)
                .status(ScrapingStatus.SCHEDULED)
                .build();
        repository.save(task);

        scraperWorkerService.scrapeAndSave(task.getId());

        return task.getId();
    }

    /**
     * Returns the current status and result if completed of a scraping task.
     * @param taskId The task ID associated with the scraping request.
     * @return Status and result of task.
     */
    public ScrapeResponseDto getScrapingTask(Long taskId) {
        Optional<ScrapingTask> optionalTask = repository.findById(taskId);

        if (optionalTask.isEmpty()) {
            return null;
        }

        ScrapingTask task = optionalTask.get();
        ScrapeResponseDto.ScrapeResponseDtoBuilder builder = ScrapeResponseDto.builder()
                .status(task.getStatus());

        if (task.getResult() != null) {
            if (task.getStatus() == ScrapingStatus.COMPLETED) {
                try {
                    List<Map<String, String>> result = objectMapper.readValue(task.getResult(), List.class);
                    builder.result(result);
                } catch (Exception e) {
                    e.printStackTrace(); // Replace with proper logging
                }
            } else if (task.getStatus() == ScrapingStatus.FAILED) {
                builder.errorMessage(task.getResult());
            }
        }

        return builder.build();
    }
}
