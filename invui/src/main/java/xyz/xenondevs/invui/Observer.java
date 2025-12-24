package xyz.xenondevs.invui;

import io.papermc.paper.threadedregions.scheduler.EntityScheduler;

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
    
    /**
     * @return An {@link EntityScheduler} that can be used to schedule tasks for this {@link Observer},
     *        or {@code null} if no such scheduler is available.
     */
    EntityScheduler getScheduler();
    
}
