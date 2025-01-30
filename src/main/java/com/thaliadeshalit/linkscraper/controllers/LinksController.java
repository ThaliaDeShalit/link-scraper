package com.thaliadeshalit.linkscraper.controllers;

import com.thaliadeshalit.linkscraper.model.dto.ScrapeRequestDto;
import com.thaliadeshalit.linkscraper.model.dto.ScrapeResponseDto;
import com.thaliadeshalit.linkscraper.services.LinkScraperService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/links")
public class LinksController {

    private final LinkScraperService scraperService;

    public LinksController(LinkScraperService scraperService) {
        this.scraperService = scraperService;
    }

    /**
     * POST /links - Start a new scraping task
     * @param request JSON with URL
     * @return task ID
     */
    @PostMapping
    public String createScrapingRequest(@RequestBody @Valid ScrapeRequestDto request) {
        Long scrapingRequestId = scraperService.initiateScraping(request.getUrl());
        return scrapingRequestId.toString();
    }

    /**
     * GET /links/{taskId} - Get scraping status and results
     * @param taskId Unique task identifier
     * @return ScrapingTask JSON or 404 if not found
     */
    @GetMapping("/{taskId}")
    public ResponseEntity<?> getScrapingResult(@PathVariable Long taskId) {
        ScrapeResponseDto response = scraperService.getScrapingTask(taskId);

        if (response == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found");
        }

        return ResponseEntity.ok(response);
    }
}
