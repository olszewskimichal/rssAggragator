package pl.michal.olszewski.rssaggregator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
@Builder
public class ItemDTO {
    private final String title;
    private final String description;
    private final String link;
    private final Instant date;
    private final String author;
}
