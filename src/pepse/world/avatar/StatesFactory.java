package pepse.utils.pepse.world.avatar;

import danogl.gui.UserInputListener;

import java.awt.event.KeyEvent;

/**
 * Factory of avatar states. implements the state design pattern.
 * @author Lihi & Omri
 */
public class StatesFactory {
    private static final int REQUIRED_FOR_JUMP = 20;
    private static final float REQUIRED_FOR_RUN = 0.5f;
    private static final int REQUIRED_FOR_DOUBLE_JUMP = 50;
    private static final int IDLE_ADDED_ENERGY = 1;
    private static final int THRESHOLD_FOR_Y_MOVEMENT = 100;

    public static final AvatarState groundState = new GroundState();
    public static final AvatarState airState = new AirState();

    /**
     * Representing the ground state - the avatar is touching the ground
     * @author Lihi & Omri
     * @see pepse.utils.pepse.world.avatar.AvatarState
     */
    private static class GroundState implements AvatarState {
        /**
         * updates the state.
         * @param avatar- the avatar object.
         * @param listener- input listener for key presses.
         */
        @Override
        public void update(Avatar avatar, UserInputListener listener) {
            // check if we fell, meaning we are in the aur and need to switch state.
            if (avatar.getVelocity().y() > THRESHOLD_FOR_Y_MOVEMENT) {
                avatar.setState(airState);
                return;
            }

            int xVelocity = 0;
            int yVelocity = 0;
            float energyCost = 0;
            float avatarEnergy = avatar.getEnergy();
            // on the ground it costs to run - so check we have enough energy. update the x velocity
            // according to the key presses.
            if (avatarEnergy >= REQUIRED_FOR_RUN) {
                if (listener.isKeyPressed(KeyEvent.VK_LEFT)) {
                    xVelocity -= 1;
                }
                if (listener.isKeyPressed(KeyEvent.VK_RIGHT)) {
                    xVelocity += 1;
                }
            }
            // if we did move, add the cost to the energy cost. and set animation
            if (xVelocity != 0){
                energyCost += REQUIRED_FOR_RUN;
            }

            // check for jump action if we have enough energy for it.
            if (avatarEnergy >= REQUIRED_FOR_JUMP) {
                if (listener.isKeyPressed(KeyEvent.VK_SPACE)) {
                    yVelocity = 1;
                    energyCost+= REQUIRED_FOR_JUMP;
                }
            }
            // move the player by calling it's function.
            avatar.movePlayer(xVelocity, yVelocity, energyCost);

            // set animation according to action
            if (yVelocity != 0) {
                avatar.jumpState();
            }
            else if (xVelocity != 0) {
                avatar.runState();
            }
            else{
                // we didn't move on any axis, we are on idle.
                avatar.addEnergy(IDLE_ADDED_ENERGY);
                avatar.idleState();
            }
        }
    }

    /**
     * Representing the air state - the avatar is in the air
     * @author Lihi & Omri
     * @see pepse.utils.pepse.world.avatar.AvatarState
     */
    private static class AirState implements AvatarState {
        /**
         * updates the state.
         * @param avatar- the avatar object.
         * @param listener- input listener for key presses.
         */
        @Override
        public void update(Avatar avatar, UserInputListener listener) {
            boolean jumping = false;
            int xVelocity = 0;
            int yVelocity = 0;
            float energyCost = 0;
            // in the air we can "run" with no costs.
            if (listener.isKeyPressed(KeyEvent.VK_LEFT)){
                xVelocity -= 1;
            }
            if (listener.isKeyPressed(KeyEvent.VK_RIGHT)){
                xVelocity += 1;
            }

            // we are already in the air - so jumping is actually double jumping.
            float avatarEnergy = avatar.getEnergy();
            if (avatar.getVelocity().y() > 0 && avatarEnergy >=REQUIRED_FOR_DOUBLE_JUMP){
                if (listener.isKeyPressed(KeyEvent.VK_SPACE)){
                    yVelocity = 1;
                    energyCost+= REQUIRED_FOR_DOUBLE_JUMP;
                }
            }
            // move the player by calling it's function.
            avatar.movePlayer(xVelocity, yVelocity, energyCost);
            // set the animation to jump if needed.
            if (xVelocity == 0 && yVelocity == 0) {
                avatar.jumpState();
            }
        }
    }
}
