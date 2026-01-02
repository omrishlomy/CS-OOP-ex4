package pepse.utils.pepse.world;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * class for creating the sky object of the simulation
 */
public class Sky {
 private static final Color BASIC_SKY_COLOR = Color.decode("#80C6E5");

 /**
  *
  * @param windowDimentions of the screen
  * @return the sky game object
  */
 public static GameObject create(Vector2 windowDimentions){
  GameObject sky = new GameObject(Vector2.ZERO,windowDimentions,new RectangleRenderable(BASIC_SKY_COLOR));
  sky.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
  sky.setTag("sky");
  return sky;
 }
}
