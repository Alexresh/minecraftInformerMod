package ru.informer.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.informer.utils.Configuration;
import ru.informer.Main;

@Environment(EnvType.CLIENT)
@Mixin(TitleScreen.class)
public class TileScreenMixin extends Screen {
    protected TileScreenMixin(Text title) {
        super(title);
    }

    @Inject(at=@At("RETURN"), method = "initWidgetsNormal")
    private void addOMFBtn(int y, int spacingY, CallbackInfo ci){
        if(Boolean.parseBoolean(Main.config.getProperty(Configuration.allProperties.OpenMinecraftFolderButton))) {
            this.addDrawableChild(ButtonWidget.builder(Text.translatable("openminecraft.button"), button -> {
                try {
                    Runtime.getRuntime().exec("explorer " + Main.runDirectory.getAbsolutePath());
                } catch (Exception e) {
                    Main.LOGGER.error("(TileScreenMixin.addOMFBtn) exec doesn't work");
                }
            }).dimensions(Main.OPENMINFOLDER_X, Main.OPENMINFOLDER_Y, Main.OPENMINFOLDER_WIDTH, Main.OPENMINFOLDER_HEIGHT).build());
        }
    }
}
