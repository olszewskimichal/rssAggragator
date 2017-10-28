package pl.michal.olszewski.rssaggregator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class ItemDTO {
    private final String title;
    private final String description;
    private final String link;
    private final Instant date;
    private final String author;
}
