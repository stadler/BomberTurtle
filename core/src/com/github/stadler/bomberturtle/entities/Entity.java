package com.github.stadler.bomberturtle.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import lombok.Data;

@Data
public class Entity {

    private String name;
    private EntityType entityType;
    private Texture texture;
    private Rectangle rectangle;
    private boolean isVisible = true;

    public Entity() {
    }

    public Entity(
            String name,
            EntityType entityType,
            Texture texture,
            Rectangle rectangle) {
        this.name = name;
        this.entityType = entityType;
        this.texture = texture;
        this.rectangle = rectangle;
    }

    public boolean isPlayer() {
        return entityType == EntityType.PLAYER;
    }

    public boolean isEnemy() {
        return entityType == EntityType.ENEMY;
    }

}
