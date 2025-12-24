package pepse.utils.pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.components.ScheduledTask;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.util.function.Consumer;

//TODO: I'm assuming the only object that collides with the fruit is the avatar. make sure that is true
// with the layers manipulation or overriding should collide with.
public class Fruit extends GameObject {
    private static final int ENERGY_ADDED = 10;
    private static final int TIME_TO_REAPEAR = 30;

    private Consumer<Integer> energyAdder;


    public Fruit(Vector2 position, Vector2 dimensions, Renderable render, Consumer<Integer> energyAdder) {
        super(position,  dimensions, render);
        this.energyAdder = energyAdder;
    }

    private void regrow(){
        renderer().setOpaqueness(1);
    }

    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        energyAdder.accept(ENERGY_ADDED);
        renderer().setOpaqueness(0);
        ScheduledTask scheduledTask = new ScheduledTask(this, TIME_TO_REAPEAR,
                false, this::regrow);
    }
}
