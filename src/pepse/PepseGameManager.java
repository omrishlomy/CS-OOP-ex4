package pepse.utils.pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.components.GameObjectPhysics;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Camera;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.utils.pepse.world.avatar.Avatar;
import pepse.utils.pepse.world.avatar.EnergyDisplay;

import java.awt.*;

// TODO: this is just to check the avatar function. NEED TO CHANGE!
public class PepseGameManager extends GameManager {
    private static final Color BACKGROUND_COLOR = Color.decode("#80C6E5");
    private static final Color PLATFORM_COLOR = new Color(212, 123, 74);

    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader, UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);

        //background
        var background =
                new GameObject(
                        Vector2.ZERO,
                        windowController.getWindowDimensions(),
                        new RectangleRenderable(BACKGROUND_COLOR)
                );
        background.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects().addGameObject(background, Layer.BACKGROUND);

        placePlatform(Vector2.of(-1024, 1000), Vector2.ONES.mult(2048));
        placePlatform(Vector2.of(-512, 700), Vector2.of(1024, 50));
        placePlatform(Vector2.of(-256, 400), Vector2.of(512, 50));
        placePlatform(Vector2.of(-128, 100), Vector2.of(256, 50));

        EnergyDisplay energyDisplay = new EnergyDisplay(Vector2.ZERO, Vector2.of(100, 40));
        gameObjects().addGameObject(energyDisplay, Layer.UI);

        var avatar = new Avatar(Vector2.of(0, 900), inputListener, imageReader,
                energyDisplay::updateEnergyDisplay);
        setCamera(new Camera(avatar, Vector2.ZERO,
                windowController.getWindowDimensions(), windowController.getWindowDimensions()));
        gameObjects().addGameObject(avatar);
    }

    private void placePlatform(Vector2 pos, Vector2 size) {
        var platform = new GameObject(pos, size, new RectangleRenderable(PLATFORM_COLOR));
        platform.physics().preventIntersectionsFromDirection(Vector2.UP);
        platform.physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
        platform.setTag("Block");
        gameObjects().addGameObject(platform, Layer.STATIC_OBJECTS);
    }

    public static void main(String[] args) {
        new PepseGameManager().run();
    }
}
