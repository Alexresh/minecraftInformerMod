package ru.informer.screens;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.text.Text;
import ru.informer.utils.SavedItemsStorage;

import java.util.ArrayList;
import java.util.List;

public class SavedItemsScreen extends Screen {

    PlayerEntity player;

    private final int itemSize = 20;
    private final int rowCount = 20;
    private int interfaceX;
    private int interfaceY = 50;

    private ArrayList<ItemStack> items;

    private final SavedItemsStorage storage = new SavedItemsStorage();

    public SavedItemsScreen(PlayerEntity player) {
        super(Text.literal("Saved items"));
        this.player = player;

    }

    @Override
    protected void init(){
        items = storage.getItems();
    }

    @Override
    public void renderInGameBackground(DrawContext context) {

    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        interfaceX = context.getScaledWindowWidth() / 2 - (rowCount * itemSize) / 2;

        super.render(context, mouseX, mouseY, delta);
        for (int i = 0; i < items.size(); i++){
            int x = (i % rowCount) * itemSize + interfaceX;
            int y = (i / rowCount) * itemSize + interfaceY;
            context.drawItemWithoutEntity(items.get(i), x + 2, y + 2);
            context.drawBorder(x,y, itemSize, itemSize, 0xFFFFFFFF);
        }
        int index = getIdFromCoordinates(mouseX, mouseY);
        if (index < items.size() && index >= 0 && client != null) {
            ItemStack item = items.get(index);
            List<Text> text = item.getTooltip(player, TooltipContext.ADVANCED);
            context.drawTooltip(client.textRenderer, text, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int index = getIdFromCoordinates(mouseX, mouseY);
        switch (button) {
            case 0 -> {
                if (index < items.size() && index >= 0) {
                    player.giveItemStack(items.get(index).copy());
                }
            }
            case 1 -> {

            }
            case 2 -> {
                if (index < items.size() && index >= 0) {
                    items.remove(index);
                }
            }
            default -> {
            }
        }


        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        player.getInventory().scrollInHotbar(verticalAmount);
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // a - 65 d - 68
        if(keyCode == 65){
            ItemStack savedItem = player.getMainHandStack().copyWithCount(1);
            if(savedItem.isOf(Items.AIR) || items.contains(savedItem)){
                return true;
            }
            items.add(savedItem);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private int getIdFromCoordinates(double mouseX, double mouseY){
        if(mouseX <= interfaceX || mouseX >= interfaceX + itemSize * rowCount || mouseY < interfaceY) return -1;
        mouseX = mouseX - interfaceX;
        mouseY = mouseY - interfaceY;
        return ((int)(mouseY / itemSize) * rowCount + (int)(mouseX / itemSize + 1)) - 1;
    }

    @Override
    public void close() {
        storage.setItems(items);
        if(client != null)
            client.setScreen(new CreativeInventoryScreen(player, FeatureSet.empty(),true));
    }
}
