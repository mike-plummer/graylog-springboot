package com.objectpartners.plummer.stockmarket.graylog;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Data
public class GelfMessage {

    private final String version = "1.1";

    @NotNull
    private String host;

    @NotNull
    @JsonProperty("short_message")
    private String shortMessage;

    @JsonProperty("full_message")
    private String fullMessage;

    private Double timestamp;

    @Min(1)
    private Integer level = 1;

    @JsonIgnore
    private Map<String, String> additionalProperties;

    @JsonUnwrapped
    public Map<String, String> getGelfAdditionalProperties() {
        if (additionalProperties == null) {
            return Collections.emptyMap();
        }
        return additionalProperties.entrySet().stream()
                .collect(Collectors.toMap(entry -> {
                    if (entry.getKey().startsWith("_")) {
                        return entry.getKey();
                    }
                    return "_" + entry.getKey();
                }, Map.Entry::getValue));
    }

    public void setTimestamp(Long millisSinceEpoch) {
        Long seconds = TimeUnit.MILLISECONDS.toSeconds(millisSinceEpoch);
        Long millis = millisSinceEpoch - seconds;
        timestamp =  seconds + millis / 1000.0;
    }
}
