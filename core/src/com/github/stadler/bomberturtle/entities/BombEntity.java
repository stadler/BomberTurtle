package com.github.stadler.bomberturtle.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BombEntity extends Entity {

    private PlayerEntity fromPlayer;

    public BombEntity() {
    }

    public BombEntity(String name, EntityType entityType, Texture texture, Rectangle rectangle, PlayerEntity fromPlayer) {
        super(name, entityType, texture, rectangle);
        this.fromPlayer = fromPlayer;
    }
}
