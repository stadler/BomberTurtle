package com.github.stadler.bomberturtle;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

record Entity(
        String name,
        EntityType entityType,
        Texture texture,
        Rectangle rectangle) {
}
