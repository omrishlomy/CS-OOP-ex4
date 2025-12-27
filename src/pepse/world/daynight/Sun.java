package pepse.utils.pepse.world.daynight;

import danogl.GameObject;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.*;

public class Sun {
 private static final Color SUN_COLOR = Color.yellow;
 private static final float SUN_SIZE = 100;
 private static final float RATIO = 0.67f; //this var is used in different classes and cant be passed -
 // maybe create a factory for all inits
 private static Vector2 initialPosition = new Vector2(0,0);
 public static GameObject create(Vector2 windoeDimensions, float cycleLength){
  Renderable renderable = new OvalRenderable(SUN_COLOR);
  initialPosition = windoeDimensions.mult(0.5f);
  Vector2 cycleCenter = new Vector2(initialPosition.x()/2,initialPosition.y()*RATIO);
  GameObject sun = new GameObject(initialPosition,Vector2.ONES.mult(SUN_SIZE),renderable);
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
