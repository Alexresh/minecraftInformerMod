package ru.informer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import static ru.informer.Visual.toggleVisual;

public class MainClient implements ClientModInitializer {

    public static final MinecraftClient minecraftClient = MinecraftClient.getInstance();

    @Override
    public void onInitializeClient() {
        KeyBinding toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.informer", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F10, "category.informer.toggle"));
        KeyBinding moveKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.move", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F9, "category.informer.toggle"));

        Visual visual = new Visual();
        visual.enableVisual();


        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.wasPressed()){
                toggleVisual = !toggleVisual;
                client.player.sendMessage(Text.literal("Visual: " + toggleVisual),true);
                if (toggleVisual) {
                    visual.enableVisual();
                } else {
                    visual.disableVisual();
                }
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while ((moveKey.wasPressed())){

            }
        });
    }




}
