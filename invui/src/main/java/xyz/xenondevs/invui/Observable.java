package xyz.xenondevs.invui;

/**
 * Something that can be observed by a {@link Observer}.
 */
public interface Observable {
    
    /**
     * Adds an {@link Observer} to this {@link Observable}.
     *
     * @param who  The {@link Observer} to that observes this {@link Observable}.
     * @param what An integer specifying what part of this {@link Observable} the {@link Observer} is observing.
     * @param how  An integer specifying how the {@link Observer} is observing this {@link Observable}.
     *             Used to {@link Observer#notifyUpdate(int) notify} the {@link Observer} about updates.
     */
    void addObserver(Observer who, int what, int how);
    
    /**
     * Removes an {@link Observer} from this {@link Observable}.
     *
     * @param who  The {@link Observer} that is no longer observes this {@link Observable}.
     * @param what An integer specifying what part of this {@link Observable} the {@link Observer} was observing.
     * @param how  An integer specifying how the {@link Observer} was observing this {@link Observable}.
     */
    void removeObserver(Observer who, int what, int how);
    
    /**
     * Instead of calling {@link Observer#notifyUpdate(int)}, {@link Observable Observables} may
     * also define an auto-update period after which {@link Observer Obervers} should automatically
     * query for updates.
     *
     * @param what An integer specifying what part of this {@link Observable} the update period is requested for.
     * @return The update period of this {@link Observable} in ticks, or {@code <= 0} for no auto-updates.
     */
    default int getUpdatePeriod(int what) {
        return -1;
    }
    
}
