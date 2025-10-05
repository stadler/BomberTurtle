package com.github.stadler.bomberturtle.entities;

import lombok.Getter;

import java.util.Arrays;

public enum EntityType {
    PATH('.'),
    WALL('x'),
    PLAYER('p'),
    ENEMY('s'),
    BOMB('b'),
    POOP('P'),
    DARTH_VADER('D'),
    UNKNOWN('?');

    @Getter
    final char entityCharacter;

    EntityType(char entityCharacter) {
        this.entityCharacter = entityCharacter;
    }

    public static EntityType getEntityTypeForChar(char entityCharacter) {
        return Arrays.stream(EntityType.values())
                .filter(entityType -> entityType.entityCharacter == entityCharacter)
                .findFirst()
                .orElse(UNKNOWN);
    }


}
