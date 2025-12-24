package pepse.utils.pepse.world.avatar;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.utils.pepse.world.LocationObserver;

import java.util.function.Consumer;

/**
 * Controls the avatar game object
 * @author Lihi & Omri
 * @see danogl.GameObject
 */
public class Avatar extends GameObject {
    private static final double TIME_BETWEEN_CLIPS = 0.3;
    private static final int MAX_NUM_OBSERVERS = 100;
    private static final Vector2 AVATAR_DIMENSIOMS = Vector2.of(40, 50);

    private static final String[] IDLE_PATHS = {"src\\assets\\idle_0.png",
            "src\\assets\\idle_1.png", "src\\assets\\idle_2.png", "src\\assets\\idle_3.png"};
    private static final String[] RUN_PATHS = {"src\\assets\\run_0.png",
            "src\\assets\\run_1.png", "src\\assets\\run_2.png", "src\\assets\\run_3.png",
            "src\\assets\\run_4.png", "src\\assets\\run_5.png"};
    private static final String[] JUMP_PATHS = {"src\\assets\\jump_0.png",
            "src\\assets\\jump_1.png", "src\\assets\\jump_2.png", "src\\assets\\jump_3.png"};

    private static final float AVATAR_X_VELOCITY = 400;
    private static final float AVATAR_Y_VELOCITY = -650;
    private static final float GRAVITY = 600;
    private static final int MAX_ENERGY = 100;
    private static final float  VERTICAL_COLLISION_NORMAL = -0.9f;

    private final LocationObserver[] locationObservers = new LocationObserver[MAX_NUM_OBSERVERS];
    private final UserInputListener inputListener;

    private int numObservers = 0;
    private int energy;
    private AvatarState avatarState = StatesFactory.groundState;
    // the consumer is the update energy display. and will be called upon changes in the energy.
    private Consumer<Integer> energyListner;
    private AnimationRenderable idleAnimation, runAnimation, jumpAnimation;

    /**
     * creates AnimationRenderable objects from the images. sets the 3 animation - idle, run, jump according
     *  to their respective images.
     * @param imageReader- used to read the images.
     */
    private void createAnimations(ImageReader imageReader) {
        // create a list of the idle images by order.
        Renderable[] idleImages = new Renderable[IDLE_PATHS.length];
        for (int i = 0; i < IDLE_PATHS.length; i++) {
            idleImages[i] = imageReader.readImage(IDLE_PATHS[i], true);
        }
        // create the idle animation from the list
        idleAnimation = new AnimationRenderable(idleImages, TIME_BETWEEN_CLIPS);

        // create a list of the run images by order.
        Renderable[] runImages = new Renderable[RUN_PATHS.length];
        for (int i = 0; i < RUN_PATHS.length; i++) {
            runImages[i] = imageReader.readImage(RUN_PATHS[i], true);
        }
        // create the run animation from the list
        runAnimation = new AnimationRenderable(runImages, TIME_BETWEEN_CLIPS);

        //create a list of the jump images by order.
        Renderable[] jumpImages = new Renderable[JUMP_PATHS.length];
        for (int i = 0; i < JUMP_PATHS.length; i++) {
            jumpImages[i] = imageReader.readImage(JUMP_PATHS[i], true);
        }
        // create the jump animation from the list
        jumpAnimation = new AnimationRenderable(jumpImages, TIME_BETWEEN_CLIPS);
    }

    /**
     * constructor for the avatar
     * @param topLeftCorner
     * @param inputListener
     * @param imageReader
     * @param energyListner
     */
    public Avatar(Vector2 topLeftCorner, UserInputListener inputListener, ImageReader imageReader,
                   Consumer<Integer> energyListner) {
        super(topLeftCorner, AVATAR_DIMENSIOMS,
                null);
        createAnimations(imageReader);
        this.renderer().setRenderable(idleAnimation);
        this.inputListener = inputListener;
        this.energyListner = energyListner;

        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        this.transform().setAccelerationY(GRAVITY);

        energy = MAX_ENERGY;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        avatarState.update(this, inputListener);

        // flip logic
        float xVel = getVelocity().x();
        if (xVel < 0) {
            renderer().setIsFlippedHorizontally(true);
        } else if (xVel > 0) {
            renderer().setIsFlippedHorizontally(false);
        }
    }

    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        // if we collided with the ground, move to ground state.
        if (other.getTag().equals("Block") && collision.getNormal().y() < VERTICAL_COLLISION_NORMAL) {
            setState(StatesFactory.groundState);
            this.transform().setVelocityY(0);
        }
    }

    public void registerLocationObserver(LocationObserver observer) {
        locationObservers[numObservers++] = observer;
    }

    private void notifyLocationObservers(float x) {
        for (int i = 0; i < numObservers; i++) {
            locationObservers[i].onLocationChanged(x);
        }
    }

    public void movePlayer(int horizontalMove,  int verticalMove, int energyCost) {
        this.transform().setVelocityX(horizontalMove * AVATAR_X_VELOCITY);

        if (horizontalMove != 0){
            // we moved horizontally so notify the observers.
            notifyLocationObservers(getTopLeftCorner().x());
            runState();
        }
        if (verticalMove != 0){
            this.transform().setVelocityY(verticalMove * AVATAR_Y_VELOCITY);
            jumpState();
            setState(StatesFactory.airState);
        }

        subtructFromEnergy(energyCost);
    }

    public int getEnergy(){
        return energy;
    }

    public void subtructFromEnergy(int amount) {
        if (amount <= energy) {
            energy = energy - amount;
            notifyEnergyDisplay();
        }
    }

    public void addEnergy(int amount) {
        energy = Math.min(energy + amount, MAX_ENERGY);
        notifyEnergyDisplay();
    }

    public void setState(AvatarState avatarState) {
        this.avatarState = avatarState;
    }

    private void notifyEnergyDisplay() {
        energyListner.accept(energy);
    }

    public void runState() {
        this.renderer().setRenderable(runAnimation);
    }

    public void jumpState() {
        this.renderer().setRenderable(jumpAnimation);
    }

    public void idleState() {
        this.renderer().setRenderable(idleAnimation);
    }

}
