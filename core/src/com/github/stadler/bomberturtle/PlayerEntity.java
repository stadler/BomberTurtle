package com.github.stadler.bomberturtle;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Duration;
import java.time.Instant;

@Data
@EqualsAndHashCode(callSuper=true)
public class PlayerEntity extends MovableEntity {

    public static final Duration BOMB_DELAY = Duration.ofSeconds(3);

    private Instant lastBomb;

    public PlayerEntity(String name, EntityType entityType, Texture texture, Rectangle rectangle, Vector2 lastMove, Instant lastBomb) {
        super(name, entityType, texture, rectangle, lastMove);
        this.lastBomb = lastBomb;
    }

}
