package pepse.utils.pepse.world.avatar;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;

import java.util.function.Supplier;

/**
 * responsible for representing the avatar's energy.
 * @author Lihi & Omri
 * @see danogl.GameObject
 */
public class EnergyDisplay extends GameObject {
    private final TextRenderable textRenderable;

    /**
     * constructor
     * @param topLeftCorner- position for the text display.
     * @param dimensions- text dimensions.
     */
    public EnergyDisplay(Vector2 topLeftCorner, Vector2 dimensions) {
        super(topLeftCorner, dimensions, null);
        // set it to be relative to the screen and not the whole world
        this.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        this.textRenderable = new TextRenderable("");
        renderer().setRenderable(textRenderable);
    }

    /**
     * updates the display upon energy changes.
     * @param energy-new energy.
     */
    public void updateEnergyDisplay(int energy) {
        textRenderable.setString(energy + "%");
    }
}
