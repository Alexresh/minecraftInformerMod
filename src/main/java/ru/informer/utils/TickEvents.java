package ru.informer.utils;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import ru.informer.Main;
import ru.informer.utils.Listeners.*;

public class TickEvents {
    public static void reload() {
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

        if(Boolean.parseBoolean(Main.config.getProperty(Configuration.allProperties.ShowEntityNbt)) && !HudNbt.registered){
            HudNbt.registered = true;
            HudRenderCallback.EVENT.register(HudNbt::run);
            Main.LOGGER.info("HudEntityNbt registered");
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
    }
}
