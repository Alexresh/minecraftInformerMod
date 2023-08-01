package ru.informer.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.informer.Main;

@Mixin(OptionsScreen.class)
public class OptionsScreenMixin extends Screen {
    protected OptionsScreenMixin(Text title) {
        super(title);
    }
    @Inject(at= @At("RETURN"), method = "init")
    private void addReloadBtn(CallbackInfo ci){
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("reload,button"), button -> {
            if(!Main.config.reload()){
                Main.LOGGER.error("(OptionScreenMixin.addReloadBtn) config file corrupted");
            }
            Main.visual.reload();
        }).dimensions(Main.OPENMINFOLDER_X , Main.OPENMINFOLDER_Y, 80, Main.OPENMINFOLDER_HEIGHT).build());
    }
}
