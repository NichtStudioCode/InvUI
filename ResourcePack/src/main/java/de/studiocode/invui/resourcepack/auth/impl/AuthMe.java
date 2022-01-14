package de.studiocode.invui.resourcepack.auth.impl;

import de.studiocode.invui.resourcepack.auth.AuthenticationService;
import fr.xephi.authme.events.LoginEvent;
import org.bukkit.event.EventHandler;

public class AuthMe extends AuthenticationService {
    
    @EventHandler
    private void handleLoginEvent(LoginEvent event) {
        handleAuthentication(event.getPlayer());
    }
    
}
