package com.rocket.user.userfeed.vo;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PageResponse {

    private Boolean lastPage;

    private Boolean firstPage;

    private Integer totalPages;

    private Long totalElements;

    private Integer size;

    private Integer currentPage;

    private List<FeedResponse> content;
}
