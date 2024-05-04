package com.github.stadler.bomberturtle;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

record Entity(
        String name,
        EntityType entityType,
        Texture texture,
        Rectangle rectangle,
        Vector2 lastMove) {
}
