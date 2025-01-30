package com.thaliadeshalit.linkscraper.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thaliadeshalit.linkscraper.model.entity.ScrapingTask;
import com.thaliadeshalit.linkscraper.model.enums.ScrapingStatus;
import com.thaliadeshalit.linkscraper.repositories.ScrapingTaskRepository;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScraperWorkerServiceTest {

    @Mock
    private ScrapingTaskRepository repository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ScraperWorkerService scraperWorkerService;

    @Test
    void shouldUpdateTaskStatusDuringScraping() {
        Long taskId = 1L;

        ScrapingTask task = ScrapingTask.builder()
                .id(taskId)
                .url("https://example.com")
                .status(ScrapingStatus.SCHEDULED)
                .build();

        when(repository.findById(taskId)).thenReturn(Optional.of(task));

        CompletableFuture<Void> future = scraperWorkerService.scrapeAndSave(taskId);

        await().atMost(5, TimeUnit.SECONDS).until(future::isDone);

        ArgumentCaptor<ScrapingTask> taskCaptor = ArgumentCaptor.forClass(ScrapingTask.class);
        verify(repository, times(2)).save(taskCaptor.capture());
        assertEquals(ScrapingStatus.COMPLETED, taskCaptor.getAllValues().get(1).getStatus());
    }

}