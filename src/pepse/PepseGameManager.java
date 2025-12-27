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
import pepse.utils.pepse.world.Block;
import pepse.utils.pepse.world.Terrain;
import pepse.utils.pepse.world.avatar.Avatar;
import pepse.utils.pepse.world.avatar.EnergyDisplay;
import pepse.utils.pepse.world.trees.Flora;
import pepse.utils.pepse.world.Sky;

import java.awt.*;
import java.util.*;
import java.util.List;

// TODO: this is just to check the avatar function. NEED TO CHANGE!
// TODO: changed the energy required for running to 0.5 to get smoother run.
public class PepseGameManager extends GameManager {
 private static final double SEED = 42;
    private static final Color PLATFORM_COLOR = new Color(212, 123, 74);

    private Avatar avatar;

    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader, UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);

        //Sky
	 	GameObject sky = Sky.create(windowController.getWindowDimensions());
        gameObjects().addGameObject(sky, Layer.BACKGROUND);

        EnergyDisplay energyDisplay = new EnergyDisplay(Vector2.ZERO, Vector2.of(100, 40));
        gameObjects().addGameObject(energyDisplay, Layer.UI);
		//Yerrain
	 Terrain terrain = new Terrain(windowController.getWindowDimensions(),SEED);
	 List<Block> blocks = terrain.createInRange(- (int)windowController.getWindowDimensions().x(),(int) windowController.getWindowDimensions().x());
	 for(Block b : blocks){
	  gameObjects().addGameObject(b, Layer.STATIC_OBJECTS);
	 }


        var avatar = new Avatar(Vector2.of(0, 0), inputListener, imageReader,
                energyDisplay::updateEnergyDisplay);
        setCamera(new Camera(avatar, Vector2.ZERO,
                windowController.getWindowDimensions(), windowController.getWindowDimensions()));
        gameObjects().addGameObject(avatar, Layer.DEFAULT);
        this.avatar = avatar;


        // add trees
       Flora flora = new Flora(Integer->1000, avatar::addEnergy, 42);
       List<GameObject> tree = flora.createInRange(-1024, 1024);
       for (GameObject gameObject : tree) {
           gameObjects().addGameObject(gameObject, Layer.STATIC_OBJECTS);
       }
    }

    private void placePlatform(Vector2 pos, Vector2 size) {
        var platform = new GameObject(pos, size, new RectangleRenderable(PLATFORM_COLOR));
        platform.physics().preventIntersectionsFromDirection(Vector2.ZERO);
        platform.physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
        platform.setTag("Block");
        gameObjects().addGameObject(platform, Layer.STATIC_OBJECTS);
    }

    public static void main(String[] args) {
        new PepseGameManager().run();
    }
}
