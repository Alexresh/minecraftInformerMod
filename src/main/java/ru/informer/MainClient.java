package ru.informer;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import ru.informer.utils.Keybinds;
import ru.informer.utils.TickEvents;

public class MainClient implements ClientModInitializer {

    public static final MinecraftClient minecraftClient = MinecraftClient.getInstance();

    @Override
    public void onInitializeClient() {
        Keybinds.register();
        TickEvents.reload();
    }

}
