package pepse.utils.pepse.world.avatar;

import danogl.gui.UserInputListener;

/**
 *An interface representing avatar states.
 * @author Lihi & Omri
 */
public interface AvatarState {
    /**
     * updates the state.
     * @param avatar- the avatar object.
     * @param listener- input listener for key presses.
     */
    void update(Avatar avatar, UserInputListener listener);
}
