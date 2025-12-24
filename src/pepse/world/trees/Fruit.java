package pepse.utils.pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.components.ScheduledTask;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.util.function.Consumer;

//TODO: I'm assuming the only object that collides with the fruit is the avatar. make sure that is true
// with the layers manipulation or overriding should collide with.

/**
 * represents a fruit in the game
 * @author Lihi & Omri
 * @see danogl.GameObject
 */
public class Fruit extends GameObject {
    private static final int ENERGY_ADDED = 10;
    private static final int TIME_TO_REAPEAR = 30;

    private Consumer<Integer> energyAdder;

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
        renderer().setOpaqueness(1);
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
        // disappear
        renderer().setOpaqueness(0);
        // schedule reappearance.
        ScheduledTask scheduledTask = new ScheduledTask(this, TIME_TO_REAPEAR,
                false, this::regrow);
    }
}
