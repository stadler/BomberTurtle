package com.github.stadler.bomberturtle;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class MovableEntity extends Entity {

    private Vector2 lastMove;

    public MovableEntity(String name, EntityType entityType, Texture texture, Rectangle rectangle, Vector2 lastMove) {
        super(name, entityType, texture, rectangle);
        this.lastMove = lastMove;
    }

}
