package com.rocket.common.response;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PageResponse<T> {

    private Boolean lastPage;

    private Boolean firstPage;

    private Integer totalPages;

    private Long totalElements;

    private Integer size;

    private Integer currentPage;

    private List<T> content;

}