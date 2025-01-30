package com.thaliadeshalit.linkscraper.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thaliadeshalit.linkscraper.model.entity.ScrapingTask;
import com.thaliadeshalit.linkscraper.model.enums.ScrapingStatus;
import com.thaliadeshalit.linkscraper.repositories.ScrapingTaskRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class ScraperWorkerService {

    private final ScrapingTaskRepository repository;
    private final ObjectMapper objectMapper;

    public ScraperWorkerService(ScrapingTaskRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    /**
     * Asynchronously scrapes a URL from the given task ID, extracts `<link>` elements,
     * and saves the results in JSON format.
     *
     * @param taskId The ID of the scraping task.
     * @return a CompletableFuture that completes when scraping is done.
     */
    @Async
    public CompletableFuture<Void> scrapeAndSave(Long taskId) {
        return CompletableFuture.runAsync(() -> {
            ScrapingTask task = repository.findById(taskId).orElse(null);
            if (task == null) {
                return;
            }

            String url = task.getUrl();
            task.setStatus(ScrapingStatus.IN_PROGRESS);
            repository.save(task);

            try {
                Document document = Jsoup.connect(url).get();
                Elements links = document.select("link");

                List<Map<String, String>> extractedLinks = new ArrayList<>();
                for (Element link : links) {
                    Map<String, String> attributes = new HashMap<>();
                    link.attributes().forEach(attr -> attributes.put(attr.getKey(), attr.getValue()));
                    extractedLinks.add(attributes);
                }

                String jsonResult = objectMapper.writeValueAsString(extractedLinks);
                task.setStatus(ScrapingStatus.COMPLETED);
                task.setResult(jsonResult);
            } catch (Exception e) {
                task.setStatus(ScrapingStatus.FAILED);
                task.setResult("Error: " + e.getMessage());
            }

            repository.save(task);
        });
    }
}
