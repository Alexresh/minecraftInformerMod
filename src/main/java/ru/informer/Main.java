package ru.informer;

import net.fabricmc.api.ModInitializer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.informer.runnables.Visual;
import ru.informer.utils.Configuration;

import java.io.File;

public class Main implements ModInitializer {

	public static final String MOD_ID = "informer";
	public static final Identifier gamemodePacket = new Identifier("flightcraft", "gamemode_change_packet");
	public static final int OPENMINFOLDER_X = 0;
	public static final int OPENMINFOLDER_Y = 0;
	public static final int OPENMINFOLDER_WIDTH = 30;
	public static final int OPENMINFOLDER_HEIGHT = 20;
	public static final File runDirectory = MinecraftClient.getInstance().runDirectory;
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final Configuration config = new Configuration();
	public static final Visual visual = new Visual();

	@Override
	public void onInitialize() {

	}
}