package com.supercoding.hanyipman.dto.vo;

import com.supercoding.hanyipman.enums.Direction;
import com.supercoding.hanyipman.error.CustomException;
import com.supercoding.hanyipman.error.domain.DirectionErrorCode;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import springfox.documentation.annotations.ApiIgnore;

@Getter

@AllArgsConstructor
public class CustomPageable {

    @ApiModelProperty(value = "페이지 번호 0", example = "0")
    private  Integer page;
    @ApiModelProperty(value = "한페이지 크기 10", example = "10")
    private  Integer size;
    @ApiModelProperty(value = "정렬 필드 id", example = "id")
    private  String sortField;
    @ApiModelProperty(value = "정렬 방향 ASC|DESC", example = "ASC|DESC")
    private  String direction;

    public Pageable from(){
        if(page==null) page = 0;
        if(size==null) size = 10;
        if(sortField == null)return PageRequest.of(page, size,Sort.Direction.ASC, "id");

        switch (Direction.getDirection(direction)) {
            case ASC:
                return PageRequest.of(page, size, Sort.Direction.ASC, sortField);
            case DESC:
                return PageRequest.of(page, size, Sort.Direction.DESC, sortField);
            default:
                throw new CustomException(DirectionErrorCode.NOT_FOUND_DIRECTION);
        }
    }

    @ApiModelProperty(hidden = true)
    public Integer getStartIndex(){
        return page*size;
    }
    @ApiModelProperty(hidden = true)
    public Integer getEndIndex(){
        return (page+1)*size;
    }
}
