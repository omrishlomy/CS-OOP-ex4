package pepse.utils.pepse.world.avatar;

import danogl.gui.UserInputListener;

import java.awt.event.KeyEvent;

public class StatesFactory {
    private static final int REQUIRED_FOR_JUMP = 20;
    private static final int REQUIRED_FOR_RUN = 2;
    private static final int REQUIRED_FOR_DOUBLE_JUMP = 50;
    private static final int IDLE_ADDED_ENERGY = 1;
    private static final int THRESHOLD_FOR_Y_MOVEMENT = 100;

    public static final AvatarState groundState = new GroundState();
    public static final AvatarState airState = new AirState();

    private static class GroundState implements AvatarState {
        @Override
        public void update(Avatar avatar, UserInputListener listener) {


            if (avatar.getVelocity().y() > THRESHOLD_FOR_Y_MOVEMENT) {
                avatar.setState(airState);
                return;
            }


            int xVelocity = 0;
            int yVelocity = 0;
            int energyCost = 0;
            int avatarEnergy = avatar.getEnergy();
            // on the ground it costs to run - so check we have enough energy
            if (avatarEnergy >= REQUIRED_FOR_RUN) {
                if (listener.isKeyPressed(KeyEvent.VK_LEFT)) {
                    xVelocity -= 1;
                }
                if (listener.isKeyPressed(KeyEvent.VK_RIGHT)) {
                    xVelocity += 1;
                }
            }

            if (xVelocity != 0){
                energyCost += REQUIRED_FOR_RUN;
            }

            if (avatarEnergy >= REQUIRED_FOR_JUMP) {
                if (listener.isKeyPressed(KeyEvent.VK_SPACE)) {
                    yVelocity = 1;
                    energyCost+= REQUIRED_FOR_JUMP;
                }
            }
            avatar.movePlayer(xVelocity, yVelocity, energyCost);

            if (xVelocity == 0 && yVelocity == 0 ){
                avatar.addEnergy(IDLE_ADDED_ENERGY);
                avatar.idleState();
            }
        }
    }

    private static class AirState implements AvatarState {
        @Override
        public void update(Avatar avatar, UserInputListener listener) {
            boolean jumping = false;
            int xVelocity = 0;
            int yVelocity = 0;
            int energyCost = 0;
            // in the air we can "run" with no costs.
            if (listener.isKeyPressed(KeyEvent.VK_LEFT)){
                xVelocity -= 1;
            }
            if (listener.isKeyPressed(KeyEvent.VK_RIGHT)){
                xVelocity += 1;
            }

            // we are already in the air - so jumping is actually double jumping.
            int avatarEnergy = avatar.getEnergy();
            if (avatar.getVelocity().y() > 0 && avatarEnergy >=REQUIRED_FOR_DOUBLE_JUMP){
                if (listener.isKeyPressed(KeyEvent.VK_SPACE)){
                    yVelocity = 1;
                    energyCost+= REQUIRED_FOR_DOUBLE_JUMP;
                }
            }
            avatar.movePlayer(xVelocity, yVelocity, energyCost);

            if (xVelocity == 0 && yVelocity == 0) {
                avatar.jumpState();
            }
        }
    }
}
