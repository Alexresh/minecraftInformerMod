package ru.informer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.world.GameMode;
import org.lwjgl.glfw.GLFW;

public class MainClient implements ClientModInitializer {

    public static final MinecraftClient minecraftClient = MinecraftClient.getInstance();

    @Override
    public void onInitializeClient() {
        KeyBinding spectatorCreativeKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("informer.spectator.creative.key", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F10, "category.informer.keys"));
        KeyBinding fakeSpectatorKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("informer.fake.spectator.key", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F9, "category.informer.keys"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (spectatorCreativeKey.wasPressed()){
                if(client.interactionManager.getCurrentGameMode() == GameMode.SURVIVAL || client.interactionManager.getCurrentGameMode() == GameMode.CREATIVE)
                {
                    client.player.networkHandler.sendCommand("gamemode spectator");
                }else{
                    client.player.networkHandler.sendCommand("gamemode survival");
                }
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (fakeSpectatorKey.wasPressed()){
                if(client.interactionManager.getCurrentGameMode() == GameMode.SURVIVAL)
                {
                    client.interactionManager.setGameMode(GameMode.SPECTATOR);
                }else{
                    client.interactionManager.setGameMode(GameMode.SURVIVAL);
                }

            }
        });
    }




}
