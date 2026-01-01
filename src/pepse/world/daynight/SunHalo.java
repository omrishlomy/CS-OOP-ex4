package pepse.utils.pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.Renderable;

import java.awt.*;

public class SunHalo extends Sun{
 private static final Color SUN_HALO_COLOR = new Color(255,255,0,20);
 public static GameObject create(GameObject sun){
  Renderable renderable = new OvalRenderable(SUN_HALO_COLOR);
  GameObject sunHalo = new GameObject(sun.getTopLeftCorner(),sun.getDimensions().mult(1.5f),renderable);
  sunHalo.addComponent(Float->sunHalo.setCenter(sun.getCenter()));
  sunHalo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
  return sunHalo;
 }
}
