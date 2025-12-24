package pepse.utils.pepse.world.trees;

import danogl.GameObject;
import danogl.components.GameObjectPhysics;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.utils.ColorSupplier;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.Random;

public class Flora {

    private static final Color WOOD_COLOR = new Color(100, 50, 20);
    private static final Color LEAF_COLOR = new Color(50, 200, 30);
    private static final int MAX_TREE_HEIGHT = 150;
    private static final int MIN_TREE_HEIGHT = 70;
    private static final int TREE_WIDTH = 25;
    private static final double RANGE_FOR_RANDOM = 1.0;
    private static final double THRESHOLD_FOR_TREE = 0.9;
    private static final double THRESHOLD_FOR_LEAFS = 0.4;
    private static final int LEAF_NUMBER = 9;
    private static final int LEAF_OUTSDIE = 3;
    private static final Vector2 LEAF_DIMENSIONS = Vector2.ONES.mult(20);
    private static final float RANGE_FOR_DELAY_TIME = 1.5f;
    private static final float LEAF_ANGLE_ROTATION = 15f;
    private static final float LEAF_ANGLE_TRANSITION_TIME= 1.5f;
    private static final float LEAF_SIZE_MANIPULATION = 1.1f;
    private static final float LEAF_SIZE_TRANSITION_TIME= 2.0f;
    private static final double THRESHOLD_FOR_FRUIT = 0.8;
    private static final Vector2 FRUIT_DIMENSIONS = Vector2.ONES.mult(18);
    private static final Color[] FRUIT_COLORS = {Color.RED, Color.ORANGE, Color.YELLOW};


    private static Random random;
    private Function<Integer, Integer> getGroundHeight;
    private Consumer<Integer> enrgyAdder;

    public Flora(Function<Integer, Integer> getGroundHeight, Consumer<Integer> energyAdder, int randomSeed) {
        this.getGroundHeight = getGroundHeight;
        random = new Random(randomSeed);
        this.enrgyAdder = energyAdder;
    }

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

    private GameObject createLeaf(Vector2 position) {
        GameObject leaf = new GameObject(position, LEAF_DIMENSIONS,
                new RectangleRenderable(ColorSupplier.approximateColor(LEAF_COLOR)));
        ScheduledTask scheduledTask = new ScheduledTask(leaf, random.nextFloat(RANGE_FOR_DELAY_TIME),
                false, ()-> leafTransitions(leaf));
        return leaf;
    }


    private List<GameObject> createTree(int x){
        List<GameObject> tree = new ArrayList<>();
        int groundHeight =  getGroundHeight.apply(x);
        int treeHeight = random.nextInt(MAX_TREE_HEIGHT - MIN_TREE_HEIGHT) + MIN_TREE_HEIGHT;

        //TODO tree trunk should be a block. than we can remove the set tag
        GameObject treeTrunck = new GameObject(Vector2.of(x, groundHeight-treeHeight),
                Vector2.of(TREE_WIDTH, treeHeight),
                new RectangleRenderable(ColorSupplier.approximateColor(WOOD_COLOR)));
        treeTrunck.setTag("Block");
        treeTrunck.physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
        treeTrunck.physics().preventIntersectionsFromDirection(Vector2.ZERO);
        tree.add(treeTrunck);

        for (int i=0; i < LEAF_NUMBER; i++){
            for (int j=0; j < LEAF_NUMBER; j++){
                Vector2 position = Vector2.of(x-LEAF_DIMENSIONS.y()*(LEAF_OUTSDIE - i),
                        groundHeight - treeHeight -LEAF_DIMENSIONS.y()*(j+1));
                if (random.nextDouble(RANGE_FOR_RANDOM) > THRESHOLD_FOR_LEAFS){
                    GameObject leaf = createLeaf(position);
                    tree.add(leaf);
                }
                if (random.nextDouble(RANGE_FOR_RANDOM) > THRESHOLD_FOR_FRUIT){
                    GameObject fruit = new Fruit(position, FRUIT_DIMENSIONS,
                            new OvalRenderable(FRUIT_COLORS[random.nextInt(FRUIT_COLORS.length)]),
                            enrgyAdder);
                    tree.add(fruit);
                }
            }
        }
        return tree;
    }

    public List<GameObject> createInRange(int minX, int maxX){
        List<GameObject> trees = new ArrayList<>();
        for (int i=minX;i<maxX;i+= 30 ){ //TODO should be Block.size instead of 30
            if (random.nextDouble(RANGE_FOR_RANDOM) > THRESHOLD_FOR_TREE){
                trees.addAll(createTree(i));
            }
        }
        return trees;
    }
}
