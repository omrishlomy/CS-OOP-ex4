package pepse.utils.pepse.world.avatar;

import danogl.GameObject;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.util.Vector2;

public class Avatar extends GameObject {
    static final Vector2 AVATAR_DIMENSIOMS = Vector2.ONES.mult(50);
    private static final String AVATAR_PICTURE_PATH =
            "src\\assets\\idle_0.png";
    static final float AVATAR_X_VELOCITY = 400;
    static final float AVATAR_Y_VELOCITY = -650;
    static final float GRAVITY = 600;
    private static final int MAX_ENERGY = 100;

    private final UserInputListener inputListener;
    private int energy;
    private AvatarState avatarState = StatesFactory.groundState;


    public  Avatar(Vector2 topLeftCorner, UserInputListener inputListener, ImageReader imageReader) {
        super(topLeftCorner, AVATAR_DIMENSIOMS,
                imageReader.readImage(AVATAR_PICTURE_PATH, true));
        this.inputListener = inputListener;
        physics().preventIntersectionsFromDirection(Vector2.ZERO);

        this.transform().setAccelerationY(GRAVITY);

        energy = MAX_ENERGY;

    }

    @Override
    public void update(float delta) {
        super.update(delta);
        avatarState.update(this, inputListener);
    }

    public int getEnergy(){
        return energy;
    }

    public void subtructFromEnergy(int amount) throws IllegalArgumentException {
        if (amount > energy){
            throw new IllegalArgumentException("amount > energy");
        }
        energy = energy - amount;
    }

    public void addEnergy(int amount) {
        energy = Math.min(energy + amount, MAX_ENERGY);
    }

    public void setState(AvatarState avatarState) {
        this.avatarState = avatarState;
    }

}
