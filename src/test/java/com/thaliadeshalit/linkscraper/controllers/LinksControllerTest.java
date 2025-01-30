package com.thaliadeshalit.linkscraper.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thaliadeshalit.linkscraper.model.dto.ScrapeRequestDto;
import com.thaliadeshalit.linkscraper.model.dto.ScrapeResponseDto;
import com.thaliadeshalit.linkscraper.model.enums.ScrapingStatus;
import com.thaliadeshalit.linkscraper.services.LinkScraperService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class LinksControllerTest {

    @Mock
    private LinkScraperService scraperService;

    @InjectMocks
    private LinksController linksController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(linksController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldStartScrapingAndReturnTaskId() throws Exception {
        ScrapeRequestDto request = new ScrapeRequestDto("https://example.com");
        Long fakeTaskId = 123L;

        when(scraperService.initiateScraping(request.getUrl())).thenReturn(fakeTaskId);

        mockMvc.perform(post("/links")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(scraperService, times(1)).initiateScraping(request.getUrl());
    }

    @Test
    void shouldReturnScrapingResultIfTaskExists() throws Exception {
        Long taskId = 123L;
        ScrapeResponseDto response = ScrapeResponseDto.builder()
                .status(ScrapingStatus.COMPLETED)
                .result(List.of(
                        Map.of("rel", "stylesheet", "href", "style.css"),
                        Map.of("rel", "icon", "href", "favicon.ico")
                ))
                .build();

        when(scraperService.getScrapingTask(taskId)).thenReturn(response);

        mockMvc.perform(get("/links/{taskId}", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.result[0].rel").value("stylesheet"))
                .andExpect(jsonPath("$.result[0].href").value("style.css"))
                .andExpect(jsonPath("$.result[1].rel").value("icon"))
                .andExpect(jsonPath("$.result[1].href").value("favicon.ico"));

        verify(scraperService, times(1)).getScrapingTask(taskId);
    }

    @Test
    void shouldReturnNotFoundIfTaskDoesNotExist() throws Exception {
        Long nonExistentTaskId = 999L;

        when(scraperService.getScrapingTask(nonExistentTaskId)).thenReturn(null);

        mockMvc.perform(get("/links/{taskId}", nonExistentTaskId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Task not found"));

        verify(scraperService, times(1)).getScrapingTask(nonExistentTaskId);
    }

    @Test
    void shouldReturnBadRequestForInvalidUrl() throws Exception {
        ScrapeRequestDto invalidRequest = new ScrapeRequestDto("invalid-url");

        mockMvc.perform(post("/links")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
