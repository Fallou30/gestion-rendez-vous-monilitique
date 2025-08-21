package com.sante.senegal.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DateNagerCountryInfo {
    private String commonName;
    private String officialName;
    private String countryCode;
    private String region;

}