package com.thaliadeshalit.linkscraper.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ScrapeRequestDto {

    @NotBlank
    @Pattern(regexp = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$")
    private String url;
}
