package ru.informer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.world.GameMode;
import org.lwjgl.glfw.GLFW;

public class MainClient implements ClientModInitializer {

    public static final MinecraftClient minecraftClient = MinecraftClient.getInstance();

    @Override
    public void onInitializeClient() {
        KeyBinding spectatorCreativeKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("informer.spectator.creative.key", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F10, "category.informer.keys"));
        KeyBinding fakeSpectatorKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("informer.fake.spectator.key", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F9, "category.informer.keys"));

        if(Boolean.parseBoolean(Main.config.getProperty(Configuration.allProperties.SetSpectatorIfFallInVoid))){
            ClientTickEvents.START_CLIENT_TICK.register(client -> {
                if(client.world != null && client.player != null && client.player.getPos().y < client.world.getDimension().minY()) {
                    if(client.interactionManager.getCurrentGameMode() == GameMode.SURVIVAL){
                        if(client.player.hasPermissionLevel(2)){
                            client.player.networkHandler.sendCommand("gamemode spectator");
                        }else{
                            client.interactionManager.setGameMode(GameMode.SPECTATOR);
                        }
                    }

                }
            });
        }

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
                PacketByteBuf data =  PacketByteBufs.create();
                if(client.interactionManager.getCurrentGameMode() == GameMode.SURVIVAL)
                {
                    data.writeInt(3);
                }else{
                    data.writeInt(0);
                }
                ClientPlayNetworking.send(Main.gamemodePacket, data);
            }
        });
    }




}
