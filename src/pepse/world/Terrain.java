package pepse.utils.pepse.world;

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
import java.util.function.BiConsumer;

/**
 * Generates and manages the game's terrain (ground).
 */
public class Terrain implements LocationObserver {

 /**
  * Baseline ground height ratio relative to the window height.
  * For example, 0.67 means the ground baseline is at 67% of the window height.
  */
 private static final double GROUND_WINDOW_RATIO = 0.67;

 /**
  * Scale factor used in the noise function. Larger values typically produce smoother variations over X.
  */
 private static final double FACTOR = 210;

 /**
  * Base color of the terrain blocks (slightly randomized per block via {@link ColorSupplier}).
  */
 private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);

 /**
  * Number of blocks stacked downward to form a full "column" of terrain at a given X location.
  */
 private static final int TERRAIN_DEPTH = 20;

 /**
  * Multiplier for how far beyond the visible area terrain should be created.
  * Helps prevent the player from reaching the edge before new terrain exists.
  */
 private static final float SAFETY_RANGE_COEFFICIENT = 1.5f;

 /**
  * Multiplier used when deciding whether blocks are far enough to be deleted.
  * Larger values mean blocks stick around longer (less frequent deletion).
  */
 private static final float DELETE_CHECK_COEFFICIENT = 2.0f;

 /**
  * Noise generator used to compute the terrain height variation per X coordinate.
  */
 private final NoiseGenerator noiseGenerator;

 /**
  * Baseline ground height at X=0 before adding noise.
  */
 private final double groundHeightAtX0;

 /**
  * Callback used to add newly created {@link Block}s into the game world at a given layer.
  */
 private final BiConsumer<Block, Integer> addGameObjects;

 /**
  * Callback used to remove {@link Block}s from the game world at a given layer.
  */
 private final BiConsumer<Block, Integer> removeGameObjects;

 /**
  * How far (in X units) beyond the current location we maintain terrain blocks for creation.
  */
 private final float bufferForCreation;

 /**
  * Rightmost X location for which blocks have been created so far.
  */
 private float maxLocationCreated = 0;

 /**
  * Leftmost X location for which blocks have been created so far.
  */
 private float minLocationCreated = 0;

 /**
  * Map from X coordinate (column position) to the list of blocks composing the terrain column at that X.
  * This allows efficient deletion of entire columns when far away.
  */
 private final HashMap<Integer, List<Block>> activeBlocks = new HashMap<>();

 /**
  * Creates a single terrain block at the given top-left corner with the given renderable,
  * adds it to the game at {@link Layer#STATIC_OBJECTS}, and returns it.
  *
  * @param topLeftCorner the top-left position of the block
  * @param renderable the visual appearance of the block
  * @return the created {@link Block}
  */
 private Block createOneBlock(Vector2 topLeftCorner, Renderable renderable)
 {
  Block block =  new Block(topLeftCorner, renderable);
  // add to game
  addGameObjects.accept(block,Layer.STATIC_OBJECTS);
  return block;
 }

 /**
  * Called whenever the Avatar location changes.
  *
  * @param location the current observed location (typically X coordinate)
  */
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

 /**
  * Removes terrain columns that are too far from the given location.
  * @param location the current observed location (typically X coordinate)
  */
 private void deleteBlocks(float location){
  List<Integer> keysToRemove = new ArrayList<>();
  // check if we need to remove blocks that are far from the avatar
  for (int x : activeBlocks.keySet()){
   if ((x > location + bufferForCreation*DELETE_CHECK_COEFFICIENT)||
		   (x < location - bufferForCreation*DELETE_CHECK_COEFFICIENT)) {
	keysToRemove.add(x);
	// delete from the game
	for (Block block : activeBlocks.get(x)){
	 removeGameObjects.accept(block, Layer.STATIC_OBJECTS);
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

 /**
  * Constructs a new {@link Terrain} generator.
  *
  * @param windowDimensions window width/height vector
  * @param seed seed value used by {@link NoiseGenerator} for deterministic terrain generation
  * @param addGameObjects callback to add created blocks into the game
  * @param removeGameObjects callback to remove blocks from the game
  */
 public Terrain(Vector2 windowDimensions,double seed, BiConsumer<Block,Integer> addGameObjects,
				BiConsumer<Block,Integer> removeGameObjects) {
  groundHeightAtX0 = windowDimensions.y()*(GROUND_WINDOW_RATIO);
  this.noiseGenerator = new NoiseGenerator(seed,(int)groundHeightAtX0);
  this.addGameObjects = addGameObjects;
  this.removeGameObjects=removeGameObjects;
  createInRange(- (int)windowDimensions.x(),(int)windowDimensions.x());
  bufferForCreation =windowDimensions.x()*SAFETY_RANGE_COEFFICIENT;
 }

 /**
  * Computes the ground height (Y coordinate) at the given X coordinate.
  * The result is the baseline ground height plus a noise value computed by {@link NoiseGenerator}.
  *
  * @param x world X coordinate
  * @return ground height (Y coordinate) at that X
  */
 public float groundHeightAt(float x){ //this method should use the noise generator to get the height
  float noise = (float) noiseGenerator.noise(x, FACTOR);
  return (float) groundHeightAtX0 + noise;
 }

 /**
  * Creates terrain columns (each column is {@link #TERRAIN_DEPTH} blocks high) for X in [minX, maxX].
  * Each column's ground level is snapped down to the nearest multiple of {@link Block#SIZE}
  * to ensure blocks align to the grid.
  * After creation, the created columns are stored in {@link #activeBlocks} and the creation range
  * bounds ({@link #minLocationCreated}, {@link #maxLocationCreated}) are updated.
  *
  * @param minX leftmost X (inclusive)
  * @param maxX rightmost X (inclusive)
  */
 public void createInRange(int minX,int maxX){
  int x=minX;
  while(x<=maxX){
   List<Block> blocksInCurrLocation = new ArrayList<>();
   float groundY = (float)(Math.floor(groundHeightAt(x)/Block.SIZE)*Block.SIZE);
   for(int height=0;height<TERRAIN_DEPTH;height++){
	Renderable renderable = new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR));
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
