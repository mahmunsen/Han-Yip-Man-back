package com.supercoding.hanyipman.dto.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> content;
    private int page;
    private int size;
    private boolean isEnd;

    public static <T> PageResponse<T> from(List<T> content, CustomPageable pageable){
        boolean isEnd = content.size() < pageable.getSize();
        return new PageResponse<>(content, pageable.getPage()+1, pageable.getSize(), isEnd);
    }
}
