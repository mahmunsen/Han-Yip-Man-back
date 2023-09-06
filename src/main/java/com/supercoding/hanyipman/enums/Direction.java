package com.supercoding.hanyipman.enums;

import com.supercoding.hanyipman.error.CustomException;
import com.supercoding.hanyipman.error.domain.DirectionErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Direction {
    DESC("DESC"),
    ASC("ASC");

    private final String direction;

    public static Direction getDirection(String direction) {
        for(Direction role: Direction.values()){
            if(role.name().equals(direction)) return role;
        }
        throw new CustomException(DirectionErrorCode.NOT_FOUND_DIRECTION);
    }
}
