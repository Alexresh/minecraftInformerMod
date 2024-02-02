package ru.informer.utils;

import com.ibm.icu.impl.ClassLoaderUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import ru.informer.Main;

import java.io.*;
import java.util.Objects;
import java.util.Properties;

@Environment(EnvType.CLIENT)
public class Configuration {
    private final File configDir = FabricLoader.getInstance().getConfigDir().toFile();
    private final File configFile = new File(configDir, "informer.properties");
    private final Properties properties = new Properties();
    private final Properties defaultProperties = new Properties();

    public enum allProperties{
        AAA_dontchangethis,
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
        HUDCountAllItems,
        PhantomsWarning,
        savedItemsPosX,
        savedItemsPosY,
        savedItemsRowsCount

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
            defaultProperties.clear();
            loadDefaultProperties(defaultProperties);
            if(!Objects.equals(getProperty(allProperties.AAA_dontchangethis), getDefaultProperty(allProperties.AAA_dontchangethis))){
                Main.LOGGER.info("(Configuration.reload) config version changed, recreating config file");
                reader.close();
                create();
            }
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
            if(configFile.exists()){
                Main.LOGGER.info(String.valueOf(configFile.delete()));
            }
            if(!configFile.createNewFile()){
                Main.LOGGER.error("(Configuration.create) the configuration file cannot be created!");
                return false;
            }else{
                //load default properties from jar
                loadDefaultProperties(properties);
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

    private void loadDefaultProperties(Properties propertiesToSave){
        try (InputStream reader = ClassLoaderUtil.getClassLoader().getResourceAsStream("informer.properties")){
            propertiesToSave.clear();
            propertiesToSave.load(reader);
            Main.LOGGER.info("(Configuration.create) load default config");
        } catch (IOException e) {
            Main.LOGGER.error("(Configuration.create) default config cannot be read");
        }
    }

    public String getDefaultProperty(allProperties property){
        return defaultProperties.getProperty(property.name());
    }
}
