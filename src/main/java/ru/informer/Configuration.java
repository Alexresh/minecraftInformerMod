package ru.informer;

import com.ibm.icu.impl.ClassLoaderUtil;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.util.Properties;

public class Configuration {
    private final File configDir = FabricLoader.getInstance().getConfigDir().toFile();
    private final File configFile = new File(configDir, "informer.properties");
    private final Properties properties = new Properties();
    public enum allProperties{
        Visual
    }

    public Configuration(){
        if(!load()) {
            Main.LOGGER.error("Config file corrupted!");
        }
    }

    public boolean load(){
        //config file check or create
        if(!configFile.exists()){
            Main.LOGGER.warn("config file does not exist, creating");
            return create();
        }
        try (Reader reader = new FileReader(configFile)){
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
                Main.LOGGER.warn("config dir does not exist, creating");
                if(!configDir.mkdir()){
                    Main.LOGGER.error("the configuration folder cannot be created!");
                    return false;
                }
            }
            if(!configFile.createNewFile()){
                Main.LOGGER.error("the configuration file cannot be created!");
                return false;
            }else{
                try (InputStream reader = ClassLoaderUtil.getClassLoader().getResourceAsStream("informer.properties")){
                    properties.load(reader);
                    Main.LOGGER.info("load default config");
                } catch (IOException e) {
                    Main.LOGGER.error("default config cannot be read");
                    return false;
                }
                //write default properties to file
                try (Writer writer = new FileWriter(configFile)) {
                    properties.store(writer,"Obabok's informer config file");
                    Main.LOGGER.info("the configuration file created!");
                }catch (IOException e){
                    return false;
                }
            }
        }catch (IOException e){
            return false;
        }
        return false;
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
