package ru.informer;

import com.ibm.icu.impl.ClassLoaderUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.util.Properties;

@Environment(EnvType.CLIENT)
public class Configuration {
    private final File configDir = FabricLoader.getInstance().getConfigDir().toFile();
    private final File configFile = new File(configDir, "informer.properties");
    private final Properties properties = new Properties();
    public enum allProperties{
        AutoClicker,
        AutoClickerNoEntitySoundNotification,
        OpenMinecraftFolderButton,
        Visual,
        MoodNotification,
        MoodSoundNotification,
        CreeperNotification,
        CreeperNotificationRadius,
        CreeperSoundNotification,
        MobHealthRender,
        MobHealthRenderRadius,
        VillagerExtendRender,
        HorseExtendRender,
        SetSpectatorIfFallInVoid,
        ToolBreakRestriction,
        HUDCountAllItems

    }

    public Configuration(){
        if(!reload()) {
            Main.LOGGER.error("(Configuration.Configuration) config file corrupted!");
        }
    }

    public boolean reload(){
        //config file check or create
        if(!configFile.exists()){
            Main.LOGGER.warn("(Configuration.reload) config file does not exist, creating");
            return create();
        }
        try (Reader reader = new FileReader(configFile)){
            properties.clear();
            properties.load(reader);
        } catch (IOException e) {
            return false;
        }
        return true;
    }
    private boolean create(){
        try{
            //config dir check or create
            if(!configDir.exists()){
                Main.LOGGER.warn("(Configuration.create) config dir does not exist, creating");
                if(!configDir.mkdir()){
                    Main.LOGGER.error("(Configuration.create) the configuration folder cannot be created!");
                    return false;
                }
            }
            if(!configFile.createNewFile()){
                Main.LOGGER.error("(Configuration.create) the configuration file cannot be created!");
                return false;
            }else{
                try (InputStream reader = ClassLoaderUtil.getClassLoader().getResourceAsStream("informer.properties")){
                    properties.load(reader);
                    Main.LOGGER.info("(Configuration.create) load default config");
                } catch (IOException e) {
                    Main.LOGGER.error("(Configuration.create) default config cannot be read");
                    return false;
                }
                //write default properties to file
                try (Writer writer = new FileWriter(configFile)) {
                    properties.store(writer,"Obabok's informer config file");
                    Main.LOGGER.info("(Configuration.create) the configuration file created!");
                    return true;
                }catch (IOException e){
                    return false;
                }
            }
        }catch (IOException e){
            return false;
        }
    }

    private boolean writePropertiesToFile(){
        try (Writer writer = new FileWriter(configFile)) {
            properties.store(writer,"Obabok's informer config file");
        }catch (IOException e){
            return false;
        }
        return true;
    }

    public String getProperty(allProperties property){
        return properties.getProperty(property.name());
    }

    public boolean setProperty(allProperties property, String value){
        properties.setProperty(property.name(), value);
        return writePropertiesToFile();
    }
}
