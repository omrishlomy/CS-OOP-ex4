package pepse.utils.pepse.world;

import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.utils.ColorSupplier;
import pepse.utils.NoiseGenerator;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

//TODO: the avatar excpect that the tag of the ground blocks is "Block".
public class Terrain implements LocationObserver {
 private static final double GROUND_WINDOW_RATIO = 0.67;
 private static final double FACTOR=210;
 private static final Color BASE_GROUND_COLOR = new Color(212,123,74);
 private static final int TERRAIN_DEPTH = 20;
 private final NoiseGenerator noiseGenerator;
 private final double groundHeightAtX0;
 private final Vector2 windowDimensions;
	private Block createOneBlock(Vector2 topLeftCorner, Renderable renderable)
	{
	 return new Block(topLeftCorner, renderable);
	}

    @Override
    public void onLocationChanged(float location) {}
	 public Terrain(Vector2 windowDimensions,double seed){
	 this.windowDimensions =  windowDimensions;
	  groundHeightAtX0 = windowDimensions.y()*(GROUND_WINDOW_RATIO);
	  this.noiseGenerator = new NoiseGenerator(seed,(int)groundHeightAtX0);
	 }
	 public float groundHeightAt(float x){ //this method should use the noise generator to get the height
	  	float noise = (float) noiseGenerator.noise(x, FACTOR);
		  return (float) groundHeightAtX0 + noise;
	 }
	 public List<Block> createInRange(int minX,int maxX){
	 Renderable renderable = new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR));
	 List<Block> blocks = new ArrayList<>((int) windowDimensions.x() / Block.SIZE);
	 int x=minX;
	 while(x<=maxX){
	  float groundY = (float)(Math.floor(groundHeightAt(x)/Block.SIZE)*Block.SIZE);
	  for(int height=0;height<TERRAIN_DEPTH;height++){
	   Vector2 topLeftCorner = new Vector2(x,groundY+height*Block.SIZE);
	   blocks.add(createOneBlock(topLeftCorner, renderable));
	  }
	  x+=Block.SIZE;
	 }
	 return blocks;
	 }
}
