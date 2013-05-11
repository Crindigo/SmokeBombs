package com.crindigo.smokebombs;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

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
    final static public Item smokeBomb = new ItemSmokeBomb(5148).setUnlocalizedName("smokeBomb");

    @SidedProxy(
            clientSide = "com.crindigo.smokebombs.ClientProxy",
            serverSide = "com.crindigo.smokebombs.CommonProxy"
    )
    public static CommonProxy proxy;

    @Mod.Init
    public void load(FMLInitializationEvent event)
    {
        proxy.registerRenderers();

        LanguageRegistry.addName(smokeBomb, "Smoke Bomb");
        for ( int i = 0; i < ItemSmokeBomb.colorNames.length; i++ ) {
            LanguageRegistry.addName(new ItemStack(smokeBomb, 1, i),
                    String.format("Smoke Bomb (%s)", ItemSmokeBomb.colorNames[i]));
        }

        GameRegistry.registerItem(smokeBomb, "smokeBomb");

        EntityRegistry.registerGlobalEntityID(EntitySmokeBomb.class, "entSmokeBomb",
                EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerModEntity(EntitySmokeBomb.class, "entSmokeBomb", 1, this, 64, 10, true);

        // gunpowder + water bottle + ink sac + sand
        for ( int i = 0; i < ItemSmokeBomb.colorNames.length; i++ ) {
            GameRegistry.addShapelessRecipe(new ItemStack(smokeBomb, 1, i),
                    Item.gunpowder, new ItemStack(Item.potion), new ItemStack(Item.dyePowder, 1, i), Block.sand);
        }

        BlockDispenser.dispenseBehaviorRegistry.putObject(smokeBomb, new DispenserBehaviorSmokeBomb());
    }
}
