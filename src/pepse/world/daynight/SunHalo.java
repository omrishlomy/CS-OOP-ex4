package pepse.utils.pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.Renderable;

import java.awt.*;
/*
class for creating the sun halo around the sun
 */
public class SunHalo extends Sun{
 private static final Color SUN_HALO_COLOR = new Color(255,255,0,20); //yellow with opacity

 /**
  *
  * @param sun game object
  * @return the sun halo game object
  */
 public static GameObject create(GameObject sun){
  Renderable renderable = new OvalRenderable(SUN_HALO_COLOR);
  GameObject sunHalo = new GameObject(sun.getTopLeftCorner(),sun.getDimensions().mult(1.5f),renderable);
  sunHalo.addComponent(Float->sunHalo.setCenter(sun.getCenter()));//ensure the sun halo surround the sun
  sunHalo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
  return sunHalo;
 }
}
