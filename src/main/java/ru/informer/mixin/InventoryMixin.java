package ru.informer.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.informer.utils.Configuration;
import ru.informer.screens.AutoClickerScreen;
import ru.informer.Main;
import ru.informer.utils.SavedItemsStorage;

import java.util.ArrayList;

@Environment(EnvType.CLIENT)
@Mixin(InventoryScreen.class)
public abstract class InventoryMixin extends AbstractInventoryScreen<PlayerScreenHandler> {


    public InventoryMixin(PlayerScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

    @Inject(at=@At("RETURN"), method = "init")
    private void addButton(CallbackInfo ci){
        if(Boolean.parseBoolean(Main.config.getProperty(Configuration.allProperties.AutoClicker))){
            this.addDrawableChild(ButtonWidget.builder(Text.translatable("autoclick.title"), button -> this.client.setScreen(new AutoClickerScreen(this))).dimensions(Main.OPENMINFOLDER_X , Main.OPENMINFOLDER_Y, 100, Main.OPENMINFOLDER_HEIGHT).build());
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(keyCode == 65){
            ItemStack savedItem = handler.getCursorStack().copyWithCount(1);
            if(!savedItem.isEmpty()){
                ArrayList<ItemStack> items = SavedItemsStorage.getItems();
                for (ItemStack leftItem: items) {
                    if(ItemStack.canCombine(leftItem, savedItem)) {
                        client.player.sendMessage(Text.translatable("saved.items.module").append(Text.translatable("saved.items.already.saved")));
                        return super.keyPressed(keyCode, scanCode, modifiers);
                    }
                }
                items.add(savedItem);
                SavedItemsStorage.setItems(items);
                client.player.sendMessage(Text.translatable("saved.items.module").append(Text.literal("+" + savedItem.getItem().toString())));
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
