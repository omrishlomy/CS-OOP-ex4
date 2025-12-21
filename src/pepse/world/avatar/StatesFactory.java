package pepse.utils.pepse.world.avatar;

import danogl.gui.UserInputListener;

import java.awt.event.KeyEvent;

public class StatesFactory {
    private static final int REQUIRED_FOR_JUMP = 20;
    private static final int REQUIRED_FOR_RUN = 2;
    private static final int REQUIRED_FOR_DOUBLE_JUMP = 50;
    private static final int IDLE_ADDED_ENERGY = 1;

    public static final AvatarState groundState = new GroundState();
    public static final AvatarState airState = new AirState();

    private static class GroundState implements AvatarState {
        @Override
        public void update(Avatar avatar, UserInputListener listener) {
            boolean running = false;
            boolean jumping = false;
            int xVelocity = 0;
            int avatarEnergy = avatar.getEnergy();
            // on the ground it costs to run - so check we have enough energy
            if (avatarEnergy >= REQUIRED_FOR_RUN) {
                if (listener.isKeyPressed(KeyEvent.VK_LEFT)) {
                    xVelocity -= Avatar.AVATAR_X_VELOCITY;
                }
                if (listener.isKeyPressed(KeyEvent.VK_RIGHT)) {
                    xVelocity += Avatar.AVATAR_X_VELOCITY;
                }
            }
            if (xVelocity != 0) {
                running = true;
            }
            avatar.transform().setVelocityX(xVelocity);

            if (avatarEnergy >= REQUIRED_FOR_JUMP) {
                if (listener.isKeyPressed(KeyEvent.VK_SPACE)) {
                    avatar.transform().setVelocityY(Avatar.AVATAR_Y_VELOCITY);
                    jumping = true;
                }
            }

            // subtract energy if running
            if (running) {
                avatar.subtructFromEnergy(REQUIRED_FOR_RUN);
            }
            // subtract energy for jumping and set state as air state
            if (jumping) {
                avatar.subtructFromEnergy(REQUIRED_FOR_JUMP);
                avatar.setState(airState);
            }
            // if idle, add energy.
            if (!running && !jumping) {
                avatar.addEnergy(IDLE_ADDED_ENERGY);
            }
        }
    }

    private static class AirState implements AvatarState {
        @Override
        public void update(Avatar avatar, UserInputListener listener) {
            boolean jumping = false;
            int xVelocity = 0;
            // in the air we can "run" with no costs.
            if (listener.isKeyPressed(KeyEvent.VK_LEFT)){
                xVelocity -= Avatar.AVATAR_X_VELOCITY;
            }
            if (listener.isKeyPressed(KeyEvent.VK_RIGHT)){
                xVelocity += Avatar.AVATAR_X_VELOCITY;
            }
            avatar.transform().setVelocityX(xVelocity);

            // we are already in the air - so jumping is actually double jumping.
            int avatarEnergy = avatar.getEnergy();
            if (avatar.getVelocity().y() > 0 && avatarEnergy >=REQUIRED_FOR_DOUBLE_JUMP){
                if (listener.isKeyPressed(KeyEvent.VK_SPACE)){
                    avatar.transform().setVelocityY(Avatar.AVATAR_Y_VELOCITY);
                    jumping = true;
                }
            }
            //subtract energy for double jumping
            if (jumping){
                avatar.subtructFromEnergy(REQUIRED_FOR_DOUBLE_JUMP);
            }
            // if we are not in the air, move to ground state.
            if (avatar.getVelocity().y() == 0) {
                avatar.setState(groundState);
            }
        }
    }
}
