package ru.informer.screens;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import ru.informer.runnables.Attacker;

@Environment(value= EnvType.CLIENT)
public class AutoClickerScreen extends Screen {

    private static final Attacker attacker = new Attacker();
    private TextFieldWidget intervalField;
    private long longInterval = 1500;
    private final Screen parent;
    public AutoClickerScreen(Screen screen) {
        super(Text.translatable("autoclick.title"));
        this.parent = screen;
    }
    @Override
    protected void init(){
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.client.setScreen(this.parent)).dimensions(this.width / 2 - 75, this.height / 2 - 10, 150, 20).build());
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("armorstand.stop"), button -> attacker.stopAttack()).dimensions(this.width / 2 + 25, this.height / 2 - 40, 50, 20).build());

        ButtonWidget startBtn = ButtonWidget.builder(Text.translatable("armorstand.start"), button -> attacker.startAttack(ArmorStandEntity.class, this.longInterval)).dimensions(this.width / 2 - 75, this.height / 2 - 40, 100, 20).build();

        this.intervalField = new TextFieldWidget(this.textRenderer, this.width / 2 - 125, this.height / 2 - 40, 48, 20, Text.translatable("armorstand.interval"));
        this.intervalField.setChangedListener(interval -> {
            Text text = this.checkInterval(interval);
            this.intervalField.setPlaceholder(Text.literal("" + this.longInterval).formatted(Formatting.DARK_GRAY));
            if (text == null) {
                this.intervalField.setEditableColor(0xE0E0E0);
                this.intervalField.setTooltip(null);
                startBtn.active = true;
            } else {
                this.intervalField.setEditableColor(0xFF5555);
                this.intervalField.setTooltip(Tooltip.of(text));
                startBtn.active = false;
            }
        });
        this.intervalField.setPlaceholder(Text.literal("" + this.longInterval).formatted(Formatting.DARK_GRAY));
        addDrawableChild(this.intervalField);
        addDrawableChild(startBtn);
    }
    @Override
    public void tick() {
        super.tick();
        if (this.intervalField != null) {
            this.intervalField.tick();
        }
    }

    private Text checkInterval(String interval) {
        if (interval.isBlank()) {
            this.longInterval = 1500;
            return null;
        }
        try {
            this.longInterval = Long.parseLong(interval);
            if (longInterval < 1) {
                return Text.translatable("invalid.interval.value");
            }
            return null;
        } catch (NumberFormatException numberFormatException) {
            this.longInterval = 1500;
            return Text.translatable("invalid.interval.value");
        }
    }


}

