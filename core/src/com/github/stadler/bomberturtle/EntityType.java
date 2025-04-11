package com.github.stadler.bomberturtle;

import java.util.Arrays;

enum EntityType {
    PATH('.'),
    WALL('x'),
    PLAYER('p'),
    ENEMY('s'),
    BOMB('b'),
    UNKNOWN('?');

    final char entityCharacter;

    EntityType(char entityCharacter) {
        this.entityCharacter = entityCharacter;
    }

    static EntityType getEntityTypeForChar(char entityCharacter) {
        return Arrays.stream(EntityType.values())
                .filter(entityType -> entityType.entityCharacter == entityCharacter)
                .findFirst()
                .orElse(UNKNOWN);
    }


}
