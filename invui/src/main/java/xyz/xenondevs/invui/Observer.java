package xyz.xenondevs.invui;

/**
 * Something that can observe an {@link Observable}.
 */
public interface Observer {
    
    /**
     * Notifies this {@link Observer} about an update of a {@link Observable} that this
     * {@link Observer} was registered to using the given number as {@code how}.
     *
     * @param i The {@code how} integer that was used to register this {@link Observer} to the {@link Observable}.
     */
    void notifyUpdate(int i);
    
}
