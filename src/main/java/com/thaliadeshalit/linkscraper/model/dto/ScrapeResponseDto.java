package com.thaliadeshalit.linkscraper.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.thaliadeshalit.linkscraper.model.enums.ScrapingStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Map;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Exclude null fields
public class ScrapeResponseDto {

    private ScrapingStatus status;

    @JsonProperty("result")
    private List<Map<String, String>> result;

    @JsonProperty("error_message")
    private String errorMessage;
}
