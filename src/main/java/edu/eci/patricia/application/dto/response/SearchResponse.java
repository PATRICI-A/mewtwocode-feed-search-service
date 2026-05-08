package edu.eci.patricia.application.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchResponse {
    private List<PatchSummaryResponse> results;
    private long total;
    private int page;
    private int size;
    private int totalPages;
}
