package xyz.xenondevs.invui.resourcepack.auth.impl;

import fr.xephi.authme.events.LoginEvent;
import org.bukkit.event.EventHandler;
import xyz.xenondevs.invui.resourcepack.auth.AuthenticationService;

public class AuthMe extends AuthenticationService {
    
    @EventHandler
    private void handleLoginEvent(LoginEvent event) {
        handleAuthentication(event.getPlayer());
    }
    
}
