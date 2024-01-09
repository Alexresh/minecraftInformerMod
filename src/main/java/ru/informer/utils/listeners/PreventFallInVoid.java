package ru.informer.utils.listeners;

import net.minecraft.client.MinecraftClient;
import net.minecraft.world.GameMode;

public class PreventFallInVoid {
    public static boolean registered = false;

    public static void run(MinecraftClient client) {
        if(client.world != null && client.player != null && client.interactionManager != null && client.player.getPos().y < client.world.getDimension().minY()) {
            if(client.interactionManager.getCurrentGameMode() == GameMode.SURVIVAL){
                if(client.player.hasPermissionLevel(2)){
                    client.player.networkHandler.sendCommand("gamemode spectator");
                }else{
                    client.interactionManager.setGameMode(GameMode.SPECTATOR);
                }
            }
        }
    }
}
