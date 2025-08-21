package com.sante.senegal.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DateNagerHoliday {
    private String date;
    private String localName;
    private String name;
    private String countryCode;
    private Boolean fixed;
    private Boolean global;
    private List<String> counties;
    private Integer launchYear;
    private List<String> types;
}


