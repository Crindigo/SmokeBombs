package com.crindigo.smokebombs;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;

import java.io.File;

@Mod(
        modid = "smokebombs",
        name = "SmokeBombs",
        version = "@VERSION@ (build @BUILD_NUMBER@)",
        certificateFingerprint = "@FINGERPRINT@"
)
@NetworkMod(
        clientSideRequired = true,
        serverSideRequired = false
)
public class SmokeBombs
{
    static public Configuration config;

    static public Item smokeBomb;

    static public double BOMB_MUDDLE_RANGE;

    @SidedProxy(
            clientSide = "com.crindigo.smokebombs.ClientProxy",
            serverSide = "com.crindigo.smokebombs.CommonProxy"
    )
    public static CommonProxy proxy;

    @Mod.PreInit
    public void preInit(FMLPreInitializationEvent event)
    {
        loadConfiguration(event.getSuggestedConfigurationFile());
    }

    @Mod.Init
    public void load(FMLInitializationEvent event)
    {
        // Register the smoke bomb item
        smokeBomb = new ItemSmokeBomb(config.getItem("smokeBomb", 5148).getInt())
                .setUnlocalizedName("smokeBomb");
        GameRegistry.registerItem(smokeBomb, "smokeBomb");

        // Register item names
        LanguageRegistry.addName(smokeBomb, "Smoke Bomb");
        for ( int i = 0; i < ItemSmokeBomb.colorNames.length; i++ ) {
            LanguageRegistry.addName(new ItemStack(smokeBomb, 1, i),
                    String.format("Smoke Bomb (%s)", ItemSmokeBomb.colorNames[i]));
        }

        // Register the smoke bomb entity
        EntityRegistry.registerModEntity(EntitySmokeBomb.class, "entSmokeBomb", 1, this, 64, 10, true);

        // needs to be after smokeBomb is set
        proxy.registerRenderers();

        // Add recipes for each color of smoke bomb
        for ( int i = 0; i < ItemSmokeBomb.colorNames.length; i++ ) {
            GameRegistry.addShapelessRecipe(new ItemStack(smokeBomb, 1, i),
                    Item.gunpowder, new ItemStack(Item.potion),
                    new ItemStack(Item.dyePowder, 1, i), Block.sand);
        }

        // Allow it to be dispensed
        BlockDispenser.dispenseBehaviorRegistry.putObject(smokeBomb, new DispenserBehaviorSmokeBomb());

        // Event handlers, currently just to stop teleportation while near an active smoke bomb
        MinecraftForge.EVENT_BUS.register(new EventHandlers());

        config.save();
    }

    private void loadConfiguration(File file)
    {
        config = new Configuration(file);
        config.load();

        Property bombEffectRadius = config.get("general", "bombEffectRadius", 3.0);
        bombEffectRadius.comment = "Radius of bounding box that affects entities";
        BOMB_MUDDLE_RANGE = bombEffectRadius.getDouble(3.0); // odd that this needs a default value, and others don't
    }
}
