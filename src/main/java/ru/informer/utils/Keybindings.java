package ru.informer.utils;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
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

public class Keybindings {
    public static final KeyBinding replaceKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("informer.replace.key", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category.informer.keys"));
    public static final KeyBinding fcSpectatorKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("informer.spectator.key", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category.informer.keys"));


    public static void register(){
        ClientTickEvents.START_CLIENT_TICK.register(Keybindings::spectatorKey);
        HudRenderCallback.EVENT.register(Keybindings::replaceKey);

    }

    private static void replaceKey(DrawContext drawContext, float v) {
        MinecraftClient client = MinecraftClient.getInstance();
        if(Keybindings.replaceKey.isPressed() && client.interactionManager != null && client.player != null && client.world != null){
            if(client.interactionManager.getCurrentGameMode() == GameMode.CREATIVE && !client.player.getInventory().getMainHandStack().isEmpty()){
                HitResult raycast =  client.crosshairTarget;
                if(raycast != null && raycast.getType() == HitResult.Type.BLOCK){
                    BlockHitResult hitResult = (BlockHitResult) raycast;
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
        while (Keybindings.fcSpectatorKey.wasPressed() && client.interactionManager != null){
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
