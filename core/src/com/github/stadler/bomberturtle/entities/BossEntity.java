package com.github.stadler.bomberturtle.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class BossEntity extends MovableEntity {

    public BossEntity(String name, EntityType entityType, Texture texture, Rectangle rectangle, Vector2 lastMove, int strength) {
        super(name, entityType, texture, rectangle, lastMove, strength);
    }
}
