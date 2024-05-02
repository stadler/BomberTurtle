package com.github.stadler.bomberturtle;

import java.util.Arrays;

enum EntityType {
    PATH('.'),
    WALL('x'),
    PLAYER('b'),
    ENEMY('s'),
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
