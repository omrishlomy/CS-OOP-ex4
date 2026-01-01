package pepse.utils.pepse.world;

import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.utils.ColorSupplier;
import pepse.utils.NoiseGenerator;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

//TODO implement the onLoactionChange methode
public class Terrain implements LocationObserver {
 private static final double GROUND_WINDOW_RATIO = 0.67;
 private static final double FACTOR=210;
 private static final Color BASE_GROUND_COLOR = new Color(212,123,74);
 private static final int TERRAIN_DEPTH = 20;
 private static final float SAFETY_RANGE_COEFFICIENT = 1.5f;
 private static final float DELETE_CHECK_COEFFICIENT = 2.0f;
 private final NoiseGenerator noiseGenerator;
 private final double groundHeightAtX0;
 private final Vector2 windowDimensions;
 private final GameObjectCollection gameObjects;
 private final float bufferForCreation;
 private float maxLocationCreated = 0;
 private float minLocationCreated = 0;
 private HashMap<Integer, List<Block>> activeBlocks = new HashMap<>();

	private Block createOneBlock(Vector2 topLeftCorner, Renderable renderable)
	{
	 Block block =  new Block(topLeftCorner, renderable);
     // add to game
        gameObjects.addGameObject(block, Layer.STATIC_OBJECTS);
        return block;
	}

    @Override
    public void onLocationChanged(float location) {
        // check if we need to add ground to our right
        if (location + bufferForCreation > maxLocationCreated){
            createInRange((int)maxLocationCreated, (int)(maxLocationCreated + bufferForCreation));
        }
        // check if we need to add ground to our left
        if (location - bufferForCreation < minLocationCreated){
            createInRange((int)(minLocationCreated - bufferForCreation), (int)minLocationCreated);
        }
        // check if we need to delete trees that are too far away
        deleteBlocks(location);
    }

    private void deleteBlocks(float location){
        List<Integer> keysToRemove = new ArrayList<>();
        // check if we need to remove blocks that are far from the avatar
        for (int x : activeBlocks.keySet()){
            if ((x > location + bufferForCreation*DELETE_CHECK_COEFFICIENT)||
                    (x < location - bufferForCreation*DELETE_CHECK_COEFFICIENT)) {
                keysToRemove.add(x);
                // delete from the game
                for (Block block : activeBlocks.get(x)){
                    gameObjects.removeGameObject(block, Layer.STATIC_OBJECTS);
                }
            }
        }
        // remove all the keys from the active tree map
        for (int key:  keysToRemove){
            activeBlocks.remove(key);
        }
        // update the range of creation accordingly
        if (!activeBlocks.isEmpty()){
            minLocationCreated = Collections.min(activeBlocks.keySet());
            maxLocationCreated = Collections.max(activeBlocks.keySet());
        }
        else{
            minLocationCreated = 0;
            maxLocationCreated = 0;
        }
    }

	 public Terrain(Vector2 windowDimensions,double seed, GameObjectCollection gameObjects) {
	 this.windowDimensions =  windowDimensions;
	  groundHeightAtX0 = windowDimensions.y()*(GROUND_WINDOW_RATIO);
	  this.noiseGenerator = new NoiseGenerator(seed,(int)groundHeightAtX0);
      this.gameObjects = gameObjects;
         createInRange(- (int)windowDimensions.x(), (int) (int)windowDimensions.x());
      bufferForCreation =windowDimensions.x()*SAFETY_RANGE_COEFFICIENT;
	 }


	 public float groundHeightAt(float x){ //this method should use the noise generator to get the height
	  	float noise = (float) noiseGenerator.noise(x, FACTOR);
		  return (float) groundHeightAtX0 + noise;
	 }


	 public void createInRange(int minX,int maxX){
        //TODO: check if we need to create the color for each block, or for the entire range
	 Renderable renderable = new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR));
	 List<Block> blocks = new ArrayList<>((int) windowDimensions.x() / Block.SIZE);
	 int x=minX;
	 while(x<=maxX){
         List<Block> blocksInCurrLocation = new ArrayList<>();
	  float groundY = (float)(Math.floor(groundHeightAt(x)/Block.SIZE)*Block.SIZE);
	  for(int height=0;height<TERRAIN_DEPTH;height++){
	   Vector2 topLeftCorner = new Vector2(x,groundY+height*Block.SIZE);
       blocksInCurrLocation.add(createOneBlock(topLeftCorner, renderable));
	  }
      activeBlocks.put(x, blocksInCurrLocation);
	  x+=Block.SIZE;
	 }
     // update min and max locations we created blocks in.
     minLocationCreated = Math.min(minLocationCreated, minX);
     maxLocationCreated = Math.max(maxLocationCreated, maxX);
	 }
}
