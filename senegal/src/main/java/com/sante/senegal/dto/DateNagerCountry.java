package com.sante.senegal.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DateNagerCountry {
    private String countryCode;
    private String name;
}

