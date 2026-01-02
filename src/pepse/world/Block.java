package pepse.utils.pepse.world;

import danogl.GameObject;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * class Block for creating a single block in the simulation
 */
public class Block extends GameObject {
 public static final int SIZE = 30;

 /**
  * Constructor
  * @param topLeftCorner of the object
  * @param renderable to be presented as a block
  */
 public Block(Vector2 topLeftCorner, Renderable renderable){
  super(topLeftCorner,Vector2.ONES.mult(SIZE),renderable);
  physics().preventIntersectionsFromDirection(Vector2.ZERO);
  physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
  this.setTag("Block");
 }
}
