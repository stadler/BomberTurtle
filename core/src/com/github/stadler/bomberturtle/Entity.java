package com.github.stadler.bomberturtle;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import lombok.Data;

import java.util.Objects;

@Data
class Entity {

    private String name;
    private EntityType entityType;
    private Texture texture;
    private Rectangle rectangle;

    Entity(
            String name,
            EntityType entityType,
            Texture texture,
            Rectangle rectangle) {
        this.name = name;
        this.entityType = entityType;
        this.texture = texture;
        this.rectangle = rectangle;
    }
}
