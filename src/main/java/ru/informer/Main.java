package ru.informer;

import net.fabricmc.api.ModInitializer;

import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class Main implements ModInitializer {
	public static final String MOD_ID = "informer";
	public static final int OPENMINFOLDER_X = 0;
	public static final int OPENMINFOLDER_Y = 0;
	public static final int OPENMINFOLDER_WIDTH = 30;
	public static final int OPENMINFOLDER_HEIGHT = 20;
	public static final File runDirectory = MinecraftClient.getInstance().runDirectory;
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static Configuration config = new Configuration();

	@Override
	public void onInitialize() {

	}
}