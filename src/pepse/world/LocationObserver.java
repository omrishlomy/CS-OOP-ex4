package pepse.utils.pepse.world;

/**
 * interface for a location observer
 * @author Lihi & Omri
 */
public interface LocationObserver {
    /**
     * methode that is called by the subject upon location change
     * @param location the new location of the subject.
     */
    void onLocationChanged(float location);
}
