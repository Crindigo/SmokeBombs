package com.crindigo.smokebombs;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: steven
 * Date: 5/9/13
 * Time: 11:18 PM
 */
public class ItemSmokeBomb extends Item
{
    public static final String[] colorNames = new String[] {
            "black", "red", "green", "brown", "blue", "purple", "cyan", "silver", "gray",
            "pink", "lime", "yellow", "lightBlue", "magenta", "orange", "white"};

    public static final int[] colorValues = new int[] {
            0x303030, 0xA00000, 0x00A000, 0x995313, 0x0000A0, 0x800080, 0x00A0A0, 0xA0A0A0, 0x707070,
            0xE0B0BA, 0x00E000, 0xE0E000, 0x7070E0, 0xE000E0, 0xE07800, 0xE0E0E0
    };

    public ItemSmokeBomb(int par1)
    {
        super(par1);
        this.maxStackSize = 16;
        this.setCreativeTab(CreativeTabs.tabMisc);
        this.setHasSubtypes(true);
    }

    @Override
    public String getUnlocalizedName(ItemStack par1ItemStack)
    {
        int i = MathHelper.clamp_int(par1ItemStack.getItemDamage(), 0, 15);
        return super.getUnlocalizedName(par1ItemStack) + "." + colorNames[i];
    }

    @Override
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.itemIcon = par1IconRegister.registerIcon("smokebombs:smokeBomb");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        if ( !par3EntityPlayer.capabilities.isCreativeMode ) {
            --par1ItemStack.stackSize;
        }

        par2World.playSoundAtEntity(par3EntityPlayer, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

        if ( !par2World.isRemote ) {
            par2World.spawnEntityInWorld(new EntitySmokeBomb(
                    par2World, par3EntityPlayer, colorValues[par1ItemStack.getItemDamage()]));
        }

        return par1ItemStack;
    }

    @Override
    public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        for ( int j = 0; j < 16; ++j ) {
            par3List.add(new ItemStack(par1, 1, j));
        }
    }
}
