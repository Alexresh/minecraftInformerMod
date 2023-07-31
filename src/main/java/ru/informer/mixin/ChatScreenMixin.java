package ru.informer.mixin;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.informer.Main;


@Mixin(ChatScreen.class)
public class ChatScreenMixin extends Screen {

    protected ChatScreenMixin(Text title) {
        super(title);
    }

    @Inject(at = @At("RETURN"), method = "init")
    private void addOMFBtn(CallbackInfo ci){
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("openminecraft.button"), button -> {
            try {
                Runtime.getRuntime().exec("explorer " + Main.runDirectory.getAbsolutePath());
            } catch (Exception e) {
                Main.LOGGER.error("Пизда");
            }
        }).dimensions(Main.OPENMINFOLDER_X , Main.OPENMINFOLDER_Y, Main.OPENMINFOLDER_WIDTH, Main.OPENMINFOLDER_HEIGHT).build());
    }

}

