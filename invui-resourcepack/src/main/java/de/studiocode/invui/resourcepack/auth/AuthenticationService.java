package de.studiocode.invui.resourcepack.auth;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public abstract class AuthenticationService implements Listener {
    
    public void handleAuthentication(Player player) {
        AuthenticationServiceManager.getInstance().getLoginHandler().accept(player);
    }
    
}
