package com.thaliadeshalit.linkscraper.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thaliadeshalit.linkscraper.model.dto.ScrapeResponseDto;
import com.thaliadeshalit.linkscraper.model.entity.ScrapingTask;
import com.thaliadeshalit.linkscraper.model.enums.ScrapingStatus;
import com.thaliadeshalit.linkscraper.repositories.ScrapingTaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LinkScraperServiceTest {

    @Mock
    private ScrapingTaskRepository repository;

    @Mock
    private ScraperWorkerService scraperWorkerService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private LinkScraperService scraperService;

    private ScrapingTask mockTask;

    private final String testUrl = "https://example.com";

    @BeforeEach
    void setUp() {
        mockTask = ScrapingTask.builder()
                .id(1L)
                .url(testUrl)
                .status(ScrapingStatus.SCHEDULED)
                .build();

        scraperService = spy(scraperService);
    }

    @Test
    void shouldInitiateScrapingWhenTaskDoesNotExist() {
       String testUrl = "https://a-new-example.com";

        ScrapingTask task = ScrapingTask.builder()
                .id(1L)
                .url(testUrl)
                .status(ScrapingStatus.SCHEDULED)
                .build();

        when(repository.save(any(ScrapingTask.class))).thenAnswer(invocation -> {
            ScrapingTask savedTask = invocation.getArgument(0);
            savedTask.setId(1L);
            return savedTask;
        });

        when(scraperWorkerService.scrapeAndSave(anyLong()))
                .thenReturn(CompletableFuture.completedFuture(null));

        Long taskId = scraperService.initiateScraping(testUrl);

        assertNotNull(taskId);
        assertEquals(1L, taskId);

        ArgumentCaptor<Long> taskIdCaptor = ArgumentCaptor.forClass(Long.class);

        verify(scraperWorkerService, times(1)).scrapeAndSave(taskIdCaptor.capture());
        assertEquals(1L, taskIdCaptor.getValue());
    }


    @Test
    void shouldReturnExistingTaskIdIfTaskAlreadyExists() {
        when(repository.findByUrl(testUrl)).thenReturn(Optional.of(mockTask));

        Long taskId = scraperService.initiateScraping(testUrl);

        assertEquals(mockTask.getId(), taskId);
        verify(repository, never()).save(any(ScrapingTask.class));
    }

    @Test
    void shouldReturnScrapeResponseForCompletedTask() throws Exception {
        String jsonResult = "[{\"rel\":\"stylesheet\",\"href\":\"style.css\"},{\"rel\":\"icon\",\"href\":\"favicon.ico\"}]";
        mockTask.setStatus(ScrapingStatus.COMPLETED);
        mockTask.setResult(jsonResult);

        when(repository.findById(1L)).thenReturn(Optional.of(mockTask));
        when(objectMapper.readValue(jsonResult, List.class)).thenReturn(List.of(
                Map.of("rel", "stylesheet", "href", "style.css"),
                Map.of("rel", "icon", "href", "favicon.ico")
        ));

        ScrapeResponseDto response = scraperService.getScrapingTask(1L);

        assertNotNull(response);
        assertEquals(ScrapingStatus.COMPLETED, response.getStatus());
        assertEquals(2, response.getResult().size());
    }

    @Test
    void shouldReturnNullIfTaskNotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        ScrapeResponseDto response = scraperService.getScrapingTask(999L);

        assertNull(response);
    }
}
