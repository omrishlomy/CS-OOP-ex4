package pepse.utils.pepse.world.avatar;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;

import java.util.function.Supplier;

public class EnergyDisplay extends GameObject {
    private final TextRenderable textRenderable;

    public EnergyDisplay(Vector2 topLeftCorner, Vector2 dimensions) {
        super(topLeftCorner, dimensions, null);
        // set it to be relative to the screen and not the whole world
        this.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        this.textRenderable = new TextRenderable("");
        renderer().setRenderable(textRenderable);
    }

    public void updateEnergyDisplay(int energy) {
        textRenderable.setString(energy + "%");
    }
}
