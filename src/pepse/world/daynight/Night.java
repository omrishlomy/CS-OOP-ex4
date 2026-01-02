package pepse.utils.pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.utils.ColorSupplier;

import java.awt.*;

/**
 * class for handling the day\night
 */
public class Night {
 private static final Color BLACK = new Color(0, 0, 0);
 private static final float MIDNIGHT_OPACITY = 0.5f;

 /**
  *
  * @param windowDimensions
  * @param cycleLength
  * @return
  */
 public static GameObject create(Vector2 windowDimensions, float cycleLength){
  Renderable renderable = new RectangleRenderable(ColorSupplier.approximateColor(BLACK));
  GameObject night = new GameObject(Vector2.ZERO,windowDimensions,renderable);
  night.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
  night.setTag("Night");
  new Transition<Float>(night,
		  night.renderer()::setOpaqueness,
		  0f,
		  MIDNIGHT_OPACITY,
		  Transition.CUBIC_INTERPOLATOR_FLOAT,
		  cycleLength,
		  Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
		  null
		  );
  return night;
 }
}
