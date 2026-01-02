package pepse.utils.pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.components.ScheduledTask;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.util.function.Consumer;

/**
 * represents a fruit in the game
 * @author Lihi & Omri
 * @see danogl.GameObject
 */
public class Fruit extends GameObject {
    private static final int INVISIBLE = 0;
    private static final int VISIBLE = 1;
    private static final int ENERGY_ADDED = 10;
    private static final int TIME_TO_REAPEAR = 30;

    private Consumer<Integer> energyAdder;
    private boolean available = true;

    /**
     * constructor
     * @param position initial position
     * @param dimensions fruit dimensions
     * @param render image renderer
     * @param energyAdder a consumer that adds energy to the user.
     */
    public Fruit(Vector2 position, Vector2 dimensions, Renderable render, Consumer<Integer> energyAdder) {
        super(position,  dimensions, render);
        this.energyAdder = energyAdder;
    }

    /**
     * methode for showing the fruit again after eaten
     */
    private void regrow(){

        renderer().setOpaqueness(VISIBLE);
        available = true;
    }

    /**
     * updates should collide with methode according to the fruit availability.
     * @param other object to check if we should collide with
     * @return boolean indicating if a collision should happen between the objects.
     */
    @Override
    public boolean shouldCollideWith(GameObject other) {
        // if the fruit is not available for eating, ignore collisions.
        if (! available){
            return false;
        }
        return super.shouldCollideWith(other);
    }

    /**
     * handles collisions with other objects
     * @param other objects we collided with
     * @param collision collision data
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        // the avatar collided with us, so we need to add energy for it
        energyAdder.accept(ENERGY_ADDED);
        // disappear and don't allow eating until regrow
        renderer().setOpaqueness(INVISIBLE);
        available = false;
        // schedule reappearance.
        ScheduledTask scheduledTask = new ScheduledTask(this, TIME_TO_REAPEAR,
                false, this::regrow);
    }
}
