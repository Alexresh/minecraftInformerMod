package ru.informer.utils.Listeners;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static ru.informer.MainClient.minecraftClient;

public class HudCountItems {

    public static boolean registered = false;
    public static void run(DrawContext drawContext, float tickDelta) {
        if(minecraftClient.player == null || minecraftClient.player.getInventory().getMainHandStack().isEmpty()) return;
        Inventory playerInventory = minecraftClient.player.getInventory();
        ItemStack mainHandStack = minecraftClient.player.getMainHandStack();
        drawContext.drawText(minecraftClient.textRenderer,
                Text.literal("" + playerInventory.count(mainHandStack.getItem())).formatted(Formatting.GOLD),
                drawContext.getScaledWindowWidth()/2 + 100,
                drawContext.getScaledWindowHeight() - 15,
                0xff000000,
                false );

    }
}
