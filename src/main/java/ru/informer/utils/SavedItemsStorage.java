package ru.informer.utils;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import ru.informer.Main;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

@Environment(EnvType.CLIENT)
public class SavedItemsStorage {
    private static boolean loaded;

    private static NbtCompound items;
    private static final Path savedItemsFile = FabricLoader.getInstance().getGameDir().resolve("savedItems.nbt");

    private static void load(){
        try{
            items = NbtIo.read(savedItemsFile);
            loaded = true;
        } catch (IOException e) {
            Main.LOGGER.error(e.getMessage());
        }

    }

    private static void write(){
        try{
            NbtIo.write(items, savedItemsFile);
        } catch (IOException e) {
            Main.LOGGER.error(e.getMessage());
        }
    }

    public static void setItems(ArrayList<ItemStack> itemStacks){
        items = new NbtCompound();
        for(int i = 0; i < itemStacks.size(); i++){
            NbtCompound compound = new NbtCompound();
            itemStacks.get(i).writeNbt(compound);
            items.put(String.valueOf(i), compound);
        }
        write();
    }

    public static ArrayList<ItemStack> getItems(){
        if(!loaded){
            load();
        }
        if(items == null) return new ArrayList<>();
        ArrayList<ItemStack> itemStacks = new ArrayList<>();
        int i = 0;
        while (items.contains(String.valueOf(i))){
            itemStacks.add(ItemStack.fromNbt(items.getCompound(String.valueOf(i))));
            i++;
        }
        return itemStacks;
    }


}
