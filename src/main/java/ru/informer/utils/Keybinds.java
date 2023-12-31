package ru.informer.utils;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameMode;
import org.lwjgl.glfw.GLFW;
import ru.informer.Main;

public class Keybinds {
    public static KeyBinding replaceKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("informer.replace.key", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F9, "category.informer.keys"));
    public static KeyBinding fcSpectatorKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("informer.spectator.key", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F9, "category.informer.keys"));
    //public static KeyBinding test = KeyBindingHelper.registerKeyBinding(new KeyBinding("informer.spectator.key1", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F10, "category.informer.keys"));

    public static void register(){
        ClientTickEvents.START_CLIENT_TICK.register(Keybinds::spectatorKey);
        ClientTickEvents.START_CLIENT_TICK.register(Keybinds::replaceKey);
    }

    private static void replaceKey(MinecraftClient client){
        if(Keybinds.replaceKey.isPressed() && client.interactionManager != null && client.player != null && client.world != null){
            if(client.interactionManager.getCurrentGameMode() == GameMode.CREATIVE && !client.player.getInventory().getMainHandStack().isEmpty()){
                HitResult raycast = client.player.raycast(5,0,true);

                if(raycast instanceof BlockHitResult hitResult){
                    Block hitBlock = client.world.getBlockState(hitResult.getBlockPos()).getBlock();
                    if(hitBlock != Blocks.AIR && hitBlock.asItem() != client.player.getInventory().getMainHandStack().getItem()){
                        client.interactionManager.attackBlock(hitResult.getBlockPos(), Direction.UP);
                        client.interactionManager.interactBlock(client.player, Hand.MAIN_HAND, hitResult);
                    }
                }
            }
        }
    }

    private static void spectatorKey(MinecraftClient client){
        while (Keybinds.fcSpectatorKey.wasPressed() && client.interactionManager != null){
            PacketByteBuf data =  PacketByteBufs.create();
            if(client.interactionManager.getCurrentGameMode() == GameMode.SURVIVAL)
            {
                data.writeInt(3);
            }else{
                data.writeInt(0);
            }
            ClientPlayNetworking.send(Main.gamemodePacket, data);
        }
    }
}
