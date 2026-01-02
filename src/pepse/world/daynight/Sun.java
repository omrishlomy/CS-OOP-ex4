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

public class Sun {
 private static final Color SUN_COLOR = Color.yellow;
 private static final float SUN_SIZE = 100;
 private static final float RATIO = 0.67f; //this var is used in different classes and cant be passed -
 // maybe create a factory for all inits
 protected static Vector2 initialPosition;
 public static GameObject create(Vector2 windoeDimensions, float cycleLength, Function<Float,Float> getInitPosition){
  Renderable renderable = new OvalRenderable(SUN_COLOR);
  initialPosition = new Vector2(0,(getInitPosition.apply(0f)-500));
  Vector2 cycleCenter = new Vector2(initialPosition.x(),initialPosition.y()*RATIO);

  GameObject sun = new GameObject(initialPosition,Vector2.ONES.mult(SUN_SIZE),renderable);
  sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
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
 public Vector2 getSunLocation(){
  return initialPosition;
 }
}
