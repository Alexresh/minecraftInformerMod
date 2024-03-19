package ru.informer.utils;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.text.Text;
import ru.informer.Main;

public class Commands {
    public static void register() {
        for (Configuration.allProperties property : Configuration.allProperties.values()) {
            if(property.name().equals("AAA_dontchangethis")) continue;
            ClientCommandRegistrationCallback.EVENT.register(
                    (dispatcher, registryAccess) -> dispatcher.register(ClientCommandManager.literal("informer")
                            .then(ClientCommandManager.literal(property.name())
                                    .then(ClientCommandManager.literal("get")
                                            .executes(context -> {
                                                context.getSource().sendFeedback(Text.literal(Main.config.getProperty(property)));
                                                return 1;
                                            }))
                                    .then(ClientCommandManager.literal("set").then(ClientCommandManager
                                            .argument("value", StringArgumentType.string())
                                                    .executes(context -> {
                                                        String new_value = StringArgumentType.getString(context, "value");
                                                        if(Main.config.setProperty(property, new_value)){
                                                            context.getSource().sendFeedback(Text.literal(property.name() + " changed to " + new_value));
                                                            Main.config.reload();
                                                            Main.visual.reload();
                                                            TickEvents.reload();
                                                        } else context.getSource().sendError(Text.literal("Something wrong"));
                                                        return 1;
                                                    })
                                            )
                                    )
                            )
                    )
            );
        }
    }
}
