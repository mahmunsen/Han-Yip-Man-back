package com.supercoding.hanyipman.dto.vo;

import com.supercoding.hanyipman.enums.Direction;
import com.supercoding.hanyipman.error.CustomException;
import com.supercoding.hanyipman.error.domain.DirectionErrorCode;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import springfox.documentation.annotations.ApiIgnore;

import java.lang.reflect.Field;

import static com.supercoding.hanyipman.enums.Direction.ASC;
import static com.supercoding.hanyipman.enums.Direction.DESC;

@Slf4j
@Setter
@AllArgsConstructor
public class CustomPageable {

    @ApiModelProperty(value = "커서", example = "0")
    private  Long cursor;
    @ApiModelProperty(value = "페이지 번호 0", example = "0")
    private  Integer page;
    @ApiModelProperty(value = "한페이지 크기 10", example = "10")
    private  Integer size;

    public Pageable from(){
        if(page==null) page = 0;
        if(size==null) size = 10;
        return PageRequest.of(page, size,Sort.Direction.ASC, "id");
    }

    @ApiModelProperty(hidden = true)
    public Long getCursor(){
        if(cursor == null) return Long.MAX_VALUE;
        return cursor;
    }
    public Integer getSize() {
        return size;
    }


//    public String getSortField(Class<?> clazz) {
//        for(Field field : clazz.getDeclaredFields()){
//            if(field.getName().equals(sortField)) return sortField;
//        }
//        return sortField;
//    }

}
