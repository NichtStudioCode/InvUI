package xyz.xenondevs.invui.resourcepack.auth;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import xyz.xenondevs.invui.InvUI;
import xyz.xenondevs.invui.resourcepack.auth.impl.AuthMe;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.function.Consumer;

public class AuthenticationServiceManager implements Listener {
    
    private static final AuthenticationServiceManager INSTANCE = new AuthenticationServiceManager();
    private final ArrayList<AuthenticationService> services = new ArrayList<>();
    private Consumer<Player> loginHandler;
    
    private AuthenticationServiceManager() {
        registerAuthenticationService("AuthMe", AuthMe.class);
        
        if (services.isEmpty()) {
            Bukkit.getPluginManager().registerEvents(this, InvUI.getInstance().getPlugin());
        }
    }
    
    public static AuthenticationServiceManager getInstance() {
        return INSTANCE;
    }
    
    private void registerAuthenticationService(String pluginName, Class<? extends AuthenticationService> serviceClass) {
        try {
            if (Bukkit.getPluginManager().isPluginEnabled(pluginName)) {
                Constructor<? extends AuthenticationService> con = serviceClass.getConstructor();
                AuthenticationService service = con.newInstance();
                Bukkit.getPluginManager().registerEvents(service, InvUI.getInstance().getPlugin());
                services.add(service);
            }
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    
    @EventHandler
    private void handleJoin(PlayerJoinEvent event) {
        loginHandler.accept(event.getPlayer());
    }
    
    public void setLoginHandler(Consumer<Player> loginHandler) {
        this.loginHandler = loginHandler;
    }
    
    public Consumer<Player> getLoginHandler() {
        return loginHandler;
    }
    
}
