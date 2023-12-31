package ru.informer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.client.MinecraftClient;
import ru.informer.utils.Keybinds;
import ru.informer.utils.Listeners.PreventFallInVoid;
import ru.informer.utils.Listeners.ToolBreakRestriction;
import ru.informer.utils.Listeners.HudCountItems;
import ru.informer.utils.Listeners.PhantomWarning;

public class MainClient implements ClientModInitializer {

    public static final MinecraftClient minecraftClient = MinecraftClient.getInstance();

    @Override
    public void onInitializeClient() {
        Keybinds.register();

        //register all ticksEvent
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if(Boolean.parseBoolean(Main.config.getProperty(Configuration.allProperties.PhantomsWarning)) && !PhantomWarning.registered){
                PhantomWarning.registered = true;
                ClientTickEvents.START_CLIENT_TICK.register(PhantomWarning::run);
                Main.LOGGER.info("PhantomWarning registered");
            }

            if(Boolean.parseBoolean(Main.config.getProperty(Configuration.allProperties.HUDCountAllItems)) && !HudCountItems.registered){
                HudCountItems.registered = true;
                HudRenderCallback.EVENT.register(HudCountItems::run);
                Main.LOGGER.info("HudCountItems registered");
            }

            if(Boolean.parseBoolean(Main.config.getProperty(Configuration.allProperties.ToolBreakRestriction)) && !ToolBreakRestriction.registered){
                ToolBreakRestriction.registered = true;
                AttackBlockCallback.EVENT.register(ToolBreakRestriction::run);
                Main.LOGGER.info("ToolBreakRestriction registered");
            }

            if(Boolean.parseBoolean(Main.config.getProperty(Configuration.allProperties.SetSpectatorIfFallInVoid)) && !PreventFallInVoid.registered){
                PreventFallInVoid.registered = true;
                ClientTickEvents.START_CLIENT_TICK.register(PreventFallInVoid::run);
                Main.LOGGER.info("PreventFallInVoid registered");
            }
        });


    }

}
