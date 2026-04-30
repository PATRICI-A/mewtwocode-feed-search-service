package edu.eci.patricia.application.usecase;

import edu.eci.patricia.application.dto.response.PatchSummaryResponse;
import edu.eci.patricia.application.service.FeedService;
import edu.eci.patricia.domain.ports.in.FeedUseCase;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.List;

@Service
public class GetFeedUseCase implements FeedUseCase{

    private final FeedService feedService;

    public GetFeedUseCase(FeedService feedService){
        this.feedService = feedService;
    }

    @Override
    public List<PatchSummaryResponse> execute(UUID userId, int page, int size){
        return feedService.execute(userId, page, size);
    }
}