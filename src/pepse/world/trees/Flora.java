package pepse.utils.pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.components.GameObjectPhysics;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.utils.ColorSupplier;
import pepse.utils.pepse.world.Block;
import pepse.utils.pepse.world.LocationObserver;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * class responsible for plants creation - tree including leaves and fruits.
 * @author Lihi & Omri
 */
public class Flora implements LocationObserver {

    private static final Color WOOD_COLOR = new Color(100, 50, 20);
    private static final Color LEAF_COLOR = new Color(50, 200, 30);
    private static final int MAX_TREE_HEIGHT = 8;
    private static final int MIN_TREE_HEIGHT = 3;
    private static final double RANGE_FOR_RANDOM = 1.0;
    private static final double THRESHOLD_FOR_TREE = 0.9;
    private static final double THRESHOLD_FOR_LEAFS = 0.4;
    private static final int LEAF_NUMBER = 6;
    private static final int LEAF_OUTSDIE = 2;
    private static final Vector2 LEAF_DIMENSIONS = Vector2.ONES.mult(20);
    private static final float RANGE_FOR_DELAY_TIME = 1.5f;
    private static final float LEAF_ANGLE_ROTATION = 15f;
    private static final float LEAF_ANGLE_TRANSITION_TIME= 1.5f;
    private static final float LEAF_SIZE_MANIPULATION = 1.1f;
    private static final float LEAF_SIZE_TRANSITION_TIME= 2.0f;
    private static final double THRESHOLD_FOR_FRUIT = 0.9;
    private static final Vector2 FRUIT_DIMENSIONS = Vector2.ONES.mult(18);
    private static final Color[] FRUIT_COLORS = {Color.RED, Color.ORANGE, Color.YELLOW};
    private static final float SAFETY_RANGE_COEFFICIENT = 1.5f;
    private static final float DELETE_CHECK_COEFFICIENT = 2.0f;

    private final float bufferForCreation;
    private final double gameSeed;
    private final Function<Float, Float> getGroundHeight;
    private final Consumer<Integer> energyAdder;
    private final BiConsumer<GameObject,Integer> addGameObjects;
    private final BiConsumer<GameObject,Integer> removeGameObjects;
    private float maxLocationCreated = 0;
    private float minLocationCreated = 0;
    private HashMap<Integer, TreeComponents> activeTrees = new HashMap<>();


    /**
     * constructor
     * @param getGroundHeight a function returning the ground height at a specific location
     * @param energyAdder a consuner that adds energy to the avatar.
     * @param gameSeed a consuner that adds energy to the avatar.
     * @param windowXSize size of the window
     */
    public Flora(Function<Float, Float> getGroundHeight, Consumer<Integer> energyAdder, double gameSeed,
                 float windowXSize, BiConsumer<GameObject,Integer> addGameObjects, BiConsumer<GameObject,Integer> removeGameObjects) {
        this.getGroundHeight = getGroundHeight;
        this.gameSeed = gameSeed;
        this.energyAdder = energyAdder;
        this.addGameObjects = addGameObjects;
        this.removeGameObjects = removeGameObjects;
        bufferForCreation = windowXSize*SAFETY_RANGE_COEFFICIENT;
        createInRange((int)-windowXSize, (int)windowXSize);
    }

    /**
     * a function that creats the needed transitions for leafs movments.
     * @param leaf a leaf object
     */
    private void leafTransitions(GameObject leaf){
        // transition for changing leaf angle
        new Transition<Float>(leaf, leaf.renderer()::setRenderableAngle,
                -LEAF_ANGLE_ROTATION, LEAF_ANGLE_ROTATION, Transition.LINEAR_INTERPOLATOR_FLOAT,
                LEAF_ANGLE_TRANSITION_TIME, Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null);

        // transition for changing leaf size
        new Transition<Vector2>(leaf, leaf::setDimensions, LEAF_DIMENSIONS,
                LEAF_DIMENSIONS.mult(LEAF_SIZE_MANIPULATION),
                Transition.LINEAR_INTERPOLATOR_VECTOR, LEAF_SIZE_TRANSITION_TIME,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);
    }

    /**
     * creating a leaf
     * @param position position for leaf
     * @param random random object to choose color
     * @return game object of a leaf
     */
    private GameObject createLeaf(Vector2 position, Random random) {
        GameObject leaf = new GameObject(position, LEAF_DIMENSIONS,
                new RectangleRenderable(ColorSupplier.approximateColor(LEAF_COLOR)));
        // leaves move only through transitions, so we can set the mass as immovable and avoid
        // unnecessary calculations.
        leaf.physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
        // schedule task for movement.
        ScheduledTask scheduledTask = new ScheduledTask(leaf, random.nextFloat(RANGE_FOR_DELAY_TIME),
                false, ()-> leafTransitions(leaf));
        // add it to game objects
        addGameObjects.accept(leaf, Layer.BACKGROUND);

        return leaf;
    }

    /**
     * creating the trunk of a tree
     * @param x location of the tree on the x-axis
     * @param groundHeight height of the ground in location x
     * @param treeHeight wanted height of the tree
     * @return list of game objects creating a trunk
     */
    private List<GameObject> createTreeTrunk(float x, float groundHeight, int treeHeight) {
        List<GameObject> trunk = new ArrayList<>();
        // create blocks to be the tree trunck according to wanted height
        for (int i = 0; i < treeHeight; i++) {
            GameObject currBlock = new Block(Vector2.of(x, groundHeight - (treeHeight-i)*Block.SIZE),
                    new RectangleRenderable(ColorSupplier.approximateColor(WOOD_COLOR)));
            trunk.add(currBlock);
            // add it to the game objects as well
            addGameObjects.accept(currBlock, Layer.STATIC_OBJECTS);
        }
        return trunk;
    }

    /**
     * creats a tree with all objects: trunk, leaves and fruits.
     * @param x x location for tree
     * @param random a random object with seed that dependent on the x position.
     * @return a list of all the tree object.
     */
    private TreeComponents createTree(float x, Random random) {
        // to ensure creation of the same tree in the same position, we'll create a Random object for each
        // tree with a seed that is dependent in the x coordinate
        List<GameObject> tree = new ArrayList<>();
        TreeComponents treeComponents = new TreeComponents();
        float groundHeight =  getGroundHeight.apply(x);
        int treeHeight = random.nextInt(MAX_TREE_HEIGHT - MIN_TREE_HEIGHT) + MIN_TREE_HEIGHT;

        treeComponents.trunk.addAll(createTreeTrunk(x, groundHeight, treeHeight));

        // loop fo creating leaves.
        for (int i=0; i < LEAF_NUMBER; i++){
            for (int j=0; j < LEAF_NUMBER; j++){
                // calculating position
                Vector2 position = Vector2.of(x-LEAF_DIMENSIONS.y()*(LEAF_OUTSDIE - i),
                        groundHeight - treeHeight*Block.SIZE -LEAF_DIMENSIONS.y()*(j+1));
                // randomly choose if to place a leaf in the position.
                if (random.nextDouble(RANGE_FOR_RANDOM) > THRESHOLD_FOR_LEAFS){
                    GameObject leaf = createLeaf(position, random);
                    treeComponents.leaves.add(leaf);
                }
                // randomly choose if to place a fruit in the position.
                if (random.nextDouble(RANGE_FOR_RANDOM) > THRESHOLD_FOR_FRUIT){
                    GameObject fruit = new Fruit(position, FRUIT_DIMENSIONS,
                            new OvalRenderable(FRUIT_COLORS[random.nextInt(FRUIT_COLORS.length)]),
                            energyAdder);
                    treeComponents.fruits.add(fruit);
                    // add to game objects as well
                    addGameObjects.accept(fruit, Layer.STATIC_OBJECTS);
                }
            }
        }
        return treeComponents;
    }

    /**
     * creates trees in a given range.
     * @param minX range left bound
     * @param maxX range right bound
     */
    public void createInRange(int minX, int maxX){
        // loop for creating trees.
        for (int i=minX;i<maxX;i+= Block.SIZE ){
            // randomly choose if to plant a tree in the current location.
            // to ensure creation of the same tree in the same position, we'll create a Random object for each
            // tree with a seed that is dependent in the x coordinate
            Random random = new Random(Objects.hash(i, gameSeed));
            if (random.nextDouble(RANGE_FOR_RANDOM) > THRESHOLD_FOR_TREE){
                activeTrees.put(i, createTree(i, random));
            }
        }
        // update min and max locations we created trees in.
        minLocationCreated = Math.min(minLocationCreated, minX);
        maxLocationCreated = Math.max(maxLocationCreated, maxX);

    }

    /**
     * controls reaction to avatar movements.
     * @param location avatar location on the x-axis
     */
    @Override
    public void onLocationChanged(float location) {
        // check if we need to add flora to our right
        if (location + bufferForCreation > maxLocationCreated){
            createInRange((int)maxLocationCreated, (int)(maxLocationCreated + bufferForCreation));
        }
        // check if we need to add flora to our left
        if (location - bufferForCreation < minLocationCreated){
            createInRange((int)(minLocationCreated - bufferForCreation), (int)minLocationCreated);
        }
        // check if we need to delete trees that are too far away
        deleteTrees(location);
    }

    /**
     * checks and deletes trees that are too far from the avatar if needed.
     * @param location avatar's location on the x-axis
     */
    private void deleteTrees(float location){
        List<Integer> keysToRemove = new ArrayList<>();
        // check if we need to remove trees that far from the avatar
        for (int x : activeTrees.keySet()){
            if ((x > location + bufferForCreation*DELETE_CHECK_COEFFICIENT)||
                    (x < location - bufferForCreation*DELETE_CHECK_COEFFICIENT)) {
                keysToRemove.add(x);
                // delete from the game
                deleteTree(activeTrees.get(x));
            }
        }
        // remove all the keys from the active tree map
        for (int key:  keysToRemove){
            activeTrees.remove(key);
        }
        // update the range of creation accordingly
        if (!activeTrees.isEmpty()){
            minLocationCreated = Collections.min(activeTrees.keySet());
            maxLocationCreated = Collections.max(activeTrees.keySet());
        }
        else{
            minLocationCreated = 0;
            maxLocationCreated = 0;
        }

    }

    /**
     * deletes a single tree from the game
     * @param treeComponents- holds the game objects that create the tree in the game.
     */
    private void deleteTree(TreeComponents treeComponents){
        // remove leaves
        for (GameObject gameObject : treeComponents.leaves){
            removeGameObjects.accept(gameObject, Layer.BACKGROUND);
        }
        //remove fruits
        for (GameObject gameObject : treeComponents.fruits){
            removeGameObjects.accept(gameObject, Layer.STATIC_OBJECTS);
        }
        // remove trunk
        for (GameObject gameObject : treeComponents.trunk){
            removeGameObjects.accept(gameObject, Layer.STATIC_OBJECTS);
        }
    }

    /**
     * a class for holding the all the game objects that together create a tree.
     * organizes to different lists: one for trunk and one for leaves and fruits.
     * will be used to separate the object to different layers.
     */
    public static class TreeComponents{
        public final List<GameObject> trunk = new ArrayList<>();
        public final List<GameObject> leaves = new ArrayList<>();
        public final List<GameObject> fruits = new ArrayList<>();
    }
}
