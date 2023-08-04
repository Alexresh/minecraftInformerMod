package ru.informer.screens;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import ru.informer.runnables.Attacker;

@Environment(EnvType.CLIENT)
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
        //interval
        this.intervalField = new TextFieldWidget(this.textRenderer,
                this.width / 2 - 50, this.height / 2 - 70, 100, 20,
                Text.translatable("autoclicker.interval"));

        //start armorstand button
        ButtonWidget startArmorstandBtn = ButtonWidget.builder(Text.translatable("autoclicker.armorstand.start"),
                button -> attacker.startAttack(ArmorStandEntity.class, this.longInterval))
                .dimensions(this.width / 2 - 50, this.height / 2 - 40, 100, 20)
                .tooltip(Tooltip.of(Text.translatable("autoclicker.armorstand.start.tooltip"))).build();
        //start nearest button
        ButtonWidget startClosestBtn = ButtonWidget.builder(Text.translatable("autoclicker.nearest.start"),
                        button -> attacker.startAttack(LivingEntity.class, this.longInterval))
                .dimensions(this.width / 2 - 50, this.height / 2 - 20, 100, 20)
                .tooltip(Tooltip.of(Text.translatable("autoclicker.nearest.start.tooltip"))).build();

        //stop button
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("autoclicker.stop"), button -> attacker.stopAttack())
                .dimensions(this.width / 2 - 50, this.height / 2, 100, 20)
                .tooltip(Tooltip.of(Text.translatable("autoclicker.stop.tooltip"))).build());


        //Back button
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.BACK, button -> this.client.setScreen(this.parent))
                .dimensions(this.width / 2 - 50, this.height / 2 + 20, 100, 20).build());

        this.intervalField.setChangedListener(interval -> {
            Text text = this.checkInterval(interval);
            this.intervalField.setPlaceholder(Text.literal("" + this.longInterval).formatted(Formatting.DARK_GRAY));
            if (text == null) {
                this.intervalField.setEditableColor(0xE0E0E0);
                this.intervalField.setTooltip(null);
                startArmorstandBtn.active = true;
                startClosestBtn.active = true;
            } else {
                this.intervalField.setEditableColor(0xFF5555);
                this.intervalField.setTooltip(Tooltip.of(text));
                startArmorstandBtn.active = false;
                startClosestBtn.active = false;
            }
        });
        this.intervalField.setPlaceholder(Text.literal("" + this.longInterval).formatted(Formatting.DARK_GRAY));
        addDrawableChild(this.intervalField);
        addDrawableChild(startArmorstandBtn);
        addDrawableChild(startClosestBtn);
    }
    @Override
    public boolean shouldPause() {
        return false;
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

