package ru.informer.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.informer.Main;
import ru.informer.utils.Configuration;
import ru.informer.utils.SavedItemsStorage;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(CreativeInventoryScreen.class)
public abstract class CreativeInventoryMixin extends AbstractInventoryScreen<CreativeInventoryScreen.CreativeScreenHandler> {

    private final int itemSize = 20;
    private final int rowsCount = Integer.parseInt(Main.config.getProperty(Configuration.allProperties.savedItemsRowsCount));
    private final int interfaceX = Integer.parseInt(Main.config.getProperty(Configuration.allProperties.savedItemsPosX));
    private final int interfaceY = Integer.parseInt(Main.config.getProperty(Configuration.allProperties.savedItemsPosY));
    private ArrayList<ItemStack> items;
    private boolean enabled = false;

    public CreativeInventoryMixin(CreativeInventoryScreen.CreativeScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }


    @Inject(method = "init", at = @At("HEAD"))
    protected void init(CallbackInfo ci){
        items = SavedItemsStorage.getItems();
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    public void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable ci) {
        int index = getIdFromCoordinates(mouseX, mouseY);
        if(client == null || client.player == null || index >= items.size() || index < 0) return;
        switch (button) {
            case 0 -> {
                ItemStack givenItem = items.get(index).copy();
                client.player.getInventory().offerOrDrop(givenItem);
                client.player.playSound(SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 1, 1);
            }
            case 1 -> {
                ItemStack givenItem = items.get(index).copy();
                givenItem.setCount(givenItem.getMaxCount());
                client.player.getInventory().offerOrDrop(givenItem);
                client.player.playSound(SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 1, 1);
            }
            case 2 -> items.remove(index);
            default -> {
            }
        }
    }

    @Inject(method = "render", at = @At("HEAD"))
    public void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci){
        if(!enabled) return;
        //interfaceX = context.getScaledWindowWidth() / 2 - (rowCount * itemSize) / 2;
        for (int i = 0; i < items.size(); i++){
            int x = (i % rowsCount) * itemSize + interfaceX;
            int y = (i / rowsCount) * itemSize + interfaceY;
            context.drawItemWithoutEntity(items.get(i), x + 2, y + 2);
            context.drawBorder(x,y, itemSize, itemSize, 0xFFFFFFFF);
        }
        int index = getIdFromCoordinates(mouseX, mouseY);
        if (index < items.size() && index >= 0 && client != null) {
            ItemStack item = items.get(index);
            List<Text> text = item.getTooltip(client.player, TooltipContext.ADVANCED);
            context.drawTooltip(client.textRenderer, text, mouseX, mouseY);
        }
    }

    @Inject(method = "keyPressed", at = @At("HEAD"))
    public void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable ci){
        //client.player.sendMessage(Text.literal(""+scanCode+" "+keyCode));
        if(keyCode == 65){
            ItemStack savedItem = handler.getCursorStack().copyWithCount(1);
            if(!savedItem.isEmpty()){
                for (ItemStack leftItem: items) {
                    if(ItemStack.canCombine(leftItem, savedItem)) {
                        client.player.sendMessage(Text.translatable("saved.items.module").append(Text.translatable("saved.items.already.saved")));
                        return;
                    }
                }
                items.add(savedItem);
            }
        }
        if(keyCode == 258 && client != null && client.player != null){
            enabled = !enabled;
            client.player.sendMessage(Text.translatable("saved.items.module").append("render-" + enabled));
        }
    }


    @Override
    public void close() {
        SavedItemsStorage.setItems(items);
        super.close();
       }

    private int getIdFromCoordinates(double mouseX, double mouseY){
        if(mouseX <= interfaceX || mouseX >= interfaceX + itemSize * rowsCount || mouseY < interfaceY) return -1;
        mouseX = mouseX - interfaceX;
        mouseY = mouseY - interfaceY;
        return ((int)(mouseY / itemSize) * rowsCount + (int)(mouseX / itemSize + 1)) - 1;
    }

}
