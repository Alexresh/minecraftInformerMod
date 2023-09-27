package ru.informer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameMode;
import org.lwjgl.glfw.GLFW;

public class MainClient implements ClientModInitializer {

    public static final MinecraftClient minecraftClient = MinecraftClient.getInstance();

    @Override
    public void onInitializeClient() {
        KeyBinding replaceKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("informer.replace.key", InputUtil.Type.KEYSYM, GLFW.GLFW_NOT_INITIALIZED, "category.informer.keys"));
        KeyBinding fcSpectatorKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("informer.spectator.key", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F9, "category.informer.keys"));

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

        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if(!world.isClient || !Boolean.parseBoolean(Main.config.getProperty(Configuration.allProperties.ToolBreakRestriction))) return ActionResult.PASS;
            if(player.getMainHandStack().isDamageable() && player.getMainHandStack().getDamage() > player.getMainHandStack().getMaxDamage() - 3){
                player.sendMessage(Text.translatable("damage.restriction").formatted(Formatting.RED).formatted(Formatting.BOLD), true);
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });

        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            if(!Boolean.parseBoolean(Main.config.getProperty(Configuration.allProperties.HUDCountAllItems))) return;
            if(minecraftClient.player == null || minecraftClient.player.getInventory().getMainHandStack().isEmpty()) return;
            Inventory playerInventory = minecraftClient.player.getInventory();
            ItemStack mainHandStack = minecraftClient.player.getMainHandStack();
            drawContext.drawText(minecraftClient.textRenderer, Text.literal("" + playerInventory.count(mainHandStack.getItem())).formatted(Formatting.GOLD),drawContext.getScaledWindowWidth()/2 + 100,drawContext.getScaledWindowHeight() - 15, 0xff000000, false );
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (fcSpectatorKey.wasPressed()){
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

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if(replaceKey.isPressed()){
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
        });
    }
}
