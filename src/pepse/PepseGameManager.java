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
import pepse.utils.pepse.world.daynight.Night;
import pepse.utils.pepse.world.daynight.Sun;
import pepse.utils.pepse.world.daynight.SunHalo;
import pepse.utils.pepse.world.trees.Flora;
import pepse.utils.pepse.world.Sky;

import java.awt.*;
import java.util.*;
import java.util.List;

// TODO: this is just to check the avatar function. NEED TO CHANGE!
// TODO: changed the energy required for running to 0.5 to get smoother run.
//TODO: set sun to always be in the center
public class PepseGameManager extends GameManager {
    private static final double SEED = 42;
    private static final double RATIO =0.67;
    private static final Color PLATFORM_COLOR = new Color(212, 123, 74);
    private static final float DAY_NIGHT_CYCLE_DURATION = 30f;
    private static final int LEAVES_AND_FRUIT_LAYER = Layer.BACKGROUND + 10;

    private Avatar avatar;

    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader,
                               UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);

        //Sky
	 	GameObject sky = Sky.create(windowController.getWindowDimensions());
        gameObjects().addGameObject(sky, Layer.BACKGROUND);
		//night
	 	GameObject night = Night.create(windowController.getWindowDimensions(),DAY_NIGHT_CYCLE_DURATION);
        gameObjects().addGameObject(night, Layer.BACKGROUND);

		 //sun
	 	GameObject sun = Sun.create(windowController.getWindowDimensions(),DAY_NIGHT_CYCLE_DURATION);
         //sun halo
         GameObject sunHalo = SunHalo.create(sun);
         gameObjects().addGameObject(sunHalo, Layer.BACKGROUND);
		 gameObjects().addGameObject(sun, Layer.BACKGROUND);

        // energy display.
        EnergyDisplay energyDisplay = new EnergyDisplay(Vector2.ZERO, Vector2.of(100, 40));
        gameObjects().addGameObject(energyDisplay, Layer.UI);
		//Terrain
        Terrain terrain = new Terrain(windowController.getWindowDimensions(),SEED, gameObjects());

        // avatar
        var avatar = new Avatar(Vector2.of(0, 0), inputListener, imageReader,
                energyDisplay::updateEnergyDisplay);
        setCamera(new Camera(avatar, Vector2.ZERO,
                windowController.getWindowDimensions(), windowController.getWindowDimensions()));
        gameObjects().addGameObject(avatar, Layer.DEFAULT);
        this.avatar = avatar;


        // add trees
       Flora flora = new Flora(terrain::groundHeightAt, avatar::addEnergy, SEED,
               windowController.getWindowDimensions().x(), gameObjects());

       // avoid checking collision between the leaves/fruits with anything that is not the avatar
        gameObjects().layers().shouldLayersCollide(LEAVES_AND_FRUIT_LAYER, Layer.STATIC_OBJECTS,
                false);
       gameObjects().layers().shouldLayersCollide(LEAVES_AND_FRUIT_LAYER, Layer.DEFAULT, true);

       // register location listeners
        avatar.registerLocationObserver(terrain);
        avatar.registerLocationObserver(flora);
    }

    //TODO delete at the end, it's was just for testing before terrain.
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
