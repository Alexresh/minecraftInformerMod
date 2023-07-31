package ru.informer.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.informer.screens.AutoClickerScreen;
import ru.informer.Main;

@Mixin(InventoryScreen.class)
public class InventoryMixin extends Screen {

    protected InventoryMixin(Text title) {
        super(title);
    }

    @Inject(at=@At("RETURN"), method = "init")
    private void addButton(CallbackInfo ci){
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("autoclick.title"), button -> this.client.setScreen(new AutoClickerScreen(this))).dimensions(Main.OPENMINFOLDER_X , Main.OPENMINFOLDER_Y, 100, Main.OPENMINFOLDER_HEIGHT).build());
    }
}
