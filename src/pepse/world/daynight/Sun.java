package pepse.utils.pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * class for creating the sun object
 */
public class Sun {
 private static final Color SUN_COLOR = Color.yellow;
 private static final float SUN_SIZE = 100;
 private static Vector2 initialPosition;

 /**
  * create the sun game object and its transition
  * @param windoeDimensions of the screen
  * @param cycleLength of the day
  * @param getInitPosition function to get the height in X0 position
  * @return the sun game object
  */
 public static GameObject create(Vector2 windoeDimensions, float cycleLength, Function<Float,Float> getInitPosition){
  Renderable renderable = new OvalRenderable(SUN_COLOR);
  Vector2 cycleCenter = new Vector2(windoeDimensions.x()/2f,getInitPosition.apply(windoeDimensions.x()));
  float radius = windoeDimensions.y()/2f;
  initialPosition = cycleCenter.add(new Vector2(0,-radius));

  GameObject sun = new GameObject(initialPosition,Vector2.ONES.mult(SUN_SIZE),renderable);
  sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
  sun.setCenter(initialPosition);
  new Transition<Float>(sun,
		  (Float angle) -> sun.setCenter(initialPosition.subtract(cycleCenter).rotated(angle).add(cycleCenter)),
		  0f,
		  360f,
		  Transition.LINEAR_INTERPOLATOR_FLOAT,
		  cycleLength,
		  Transition.TransitionType.TRANSITION_LOOP,
		  null
		  );
  return sun;
 }
}
