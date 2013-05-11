package com.crindigo.smokebombs;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntitySmokeBomb extends Entity implements IProjectile
{
    final static int DW_COLOR = 16;

    private int xTile = -1;
    private int yTile = -1;
    private int zTile = -1;
    private int inTile = 0;
    protected boolean inGround = false;

    private int smokeCountdown = 40;
    private int smokeLeft = 200;

    /**
     * Is the entity that throws this 'thing' (snowball, ender pearl, eye of ender or potion)
     */
    private EntityLiving thrower;
    private String throwerName = null;
    private int ticksInGround;
    private int ticksInAir = 0;

    public EntitySmokeBomb(World par1World)
    {
        super(par1World);
        this.setSize(0.25F, 0.25F);
    }

    protected void entityInit()
    {
        this.dataWatcher.addObject(DW_COLOR, 0);
    }

    @SideOnly(Side.CLIENT)

    /**
     * Checks if the entity is in range to render by using the past in distance and comparing it to its average edge
     * length * 64 * renderDistanceWeight Args: distance
     */
    public boolean isInRangeToRenderDist(double par1)
    {
        double d1 = this.boundingBox.getAverageEdgeLength() * 4.0D;
        d1 *= 64.0D;
        return par1 < d1 * d1;
    }

    public EntitySmokeBomb(World par1World, EntityLiving par2EntityLiving)
    {
        super(par1World);
        this.thrower = par2EntityLiving;
        this.setSize(0.25F, 0.25F);
        this.setLocationAndAngles(par2EntityLiving.posX,
                par2EntityLiving.posY + (double)par2EntityLiving.getEyeHeight(),
                par2EntityLiving.posZ,
                par2EntityLiving.rotationYaw, par2EntityLiving.rotationPitch);
        this.posX -= (double)(MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
        this.posY -= 0.1;
        this.posZ -= (double)(MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
        this.setPosition(this.posX, this.posY, this.posZ);
        this.yOffset = 0.0F;
        float f = 0.4F;
        this.motionX = (double)(-MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI)
                * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI) * f);
        this.motionZ = (double)(MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI)
                * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI) * f);
        this.motionY = (double)(-MathHelper.sin(
                this.rotationPitch / 180.0F * (float)Math.PI) * f);
        this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, 1.0F, 1.0F);
    }

    public EntitySmokeBomb(World par1World, double par2, double par4, double par6)
    {
        super(par1World);
        this.ticksInGround = 0;
        this.setSize(0.25F, 0.25F);
        this.setPosition(par2, par4, par6);
        this.yOffset = 0.0F;
    }

    public EntitySmokeBomb(World world, EntityLiving living, int color)
    {
        this(world, living);

        this.setColor(color);
    }

    public void setColor(int color)
    {
        this.dataWatcher.updateObject(DW_COLOR, color);
    }

    /**
     * Similar to setArrowHeading, it's point the throwable entity to a x, y, z direction.
     */
    public void setThrowableHeading(double par1, double par3, double par5, float par7, float par8)
    {
        float f2 = MathHelper.sqrt_double(par1 * par1 + par3 * par3 + par5 * par5);
        par1 /= (double)f2;
        par3 /= (double)f2;
        par5 /= (double)f2;
        par1 += this.rand.nextGaussian() * 0.0075 * (double)par8;
        par3 += this.rand.nextGaussian() * 0.0075 * (double)par8;
        par5 += this.rand.nextGaussian() * 0.0075 * (double)par8;
        par1 *= (double)par7;
        par3 *= (double)par7;
        par5 *= (double)par7;
        this.motionX = par1;
        this.motionY = par3;
        this.motionZ = par5;
        float f3 = MathHelper.sqrt_double(par1 * par1 + par5 * par5);
        this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(par1, par5) * 180.0D / Math.PI);
        this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(par3, (double)f3) * 180.0D / Math.PI);
        this.ticksInGround = 0;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Sets the velocity to the args. Args: x, y, z
     */
    public void setVelocity(double par1, double par3, double par5)
    {
        this.motionX = par1;
        this.motionY = par3;
        this.motionZ = par5;

        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F)
        {
            float f = MathHelper.sqrt_double(par1 * par1 + par5 * par5);
            this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(par1, par5) * 180.0D / Math.PI);
            this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(par3, (double)f) * 180.0D / Math.PI);
        }
    }

    public void onUpdate()
    {
        super.onUpdate();

        this.motionX *= 0.98;
        this.motionY -= this.getGravityVelocity();
        this.motionZ *= 0.98;

        if ( this.onGround ) {
            this.motionX *= 0.6;
            this.motionZ *= 0.6;
            this.motionY = 0.0;
        }

        this.moveEntity(this.motionX, this.motionY, this.motionZ);

        handleSmoke();
    }

    private void handleSmoke()
    {
        if ( smokeCountdown > 0 ) {
            smokeCountdown--;
        }

        if ( smokeLeft <= 0 ) {
            this.setDead();
        } else if ( smokeCountdown <= 0 ) {
            smokeLeft--;

            if ( this.worldObj.isRemote ) {
                float randFactor = (float)(Math.random() * .3f);

                int color = this.dataWatcher.getWatchableObjectInt(DW_COLOR);
                float emitRed = ((color >> 16) & 255) / 255f;
                float emitGreen = ((color >> 8) & 255) / 255f;
                float emitBlue = (color & 255) / 255f;

                float r = emitRed - randFactor;
                float g = emitGreen - randFactor;
                float b = emitBlue - randFactor;

                r = MathHelper.clamp_float(r, 0.0f, 1.0f);
                g = MathHelper.clamp_float(g, 0.0f, 1.0f);
                b = MathHelper.clamp_float(b, 0.0f, 1.0f);

                Minecraft mc = FMLClientHandler.instance().getClient();
                mc.effectRenderer.addEffect(new CustomSmokeFX(
                        this.worldObj, this.posX, this.posY + 0.1, this.posZ, r, g, b));
            }
        }
    }

    /**
     * Gets the amount of gravity to apply to the thrown entity with each tick.
     */
    protected float getGravityVelocity()
    {
        return 0.03F;
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        par1NBTTagCompound.setShort("xTile", (short)this.xTile);
        par1NBTTagCompound.setShort("yTile", (short)this.yTile);
        par1NBTTagCompound.setShort("zTile", (short)this.zTile);
        par1NBTTagCompound.setByte("inTile", (byte)this.inTile);
        par1NBTTagCompound.setByte("inGround", (byte)(this.inGround ? 1 : 0));

        if ((this.throwerName == null || this.throwerName.length() == 0) && this.thrower != null && this.thrower instanceof EntityPlayer)
        {
            this.throwerName = this.thrower.getEntityName();
        }

        par1NBTTagCompound.setString("ownerName", this.throwerName == null ? "" : this.throwerName);

        par1NBTTagCompound.setByte("smokeCountdown", (byte) smokeCountdown);
        par1NBTTagCompound.setByte("smokeLeft", (byte) smokeLeft);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        this.xTile = par1NBTTagCompound.getShort("xTile");
        this.yTile = par1NBTTagCompound.getShort("yTile");
        this.zTile = par1NBTTagCompound.getShort("zTile");
        this.inTile = par1NBTTagCompound.getByte("inTile") & 255;
        this.inGround = par1NBTTagCompound.getByte("inGround") == 1;
        this.throwerName = par1NBTTagCompound.getString("ownerName");

        if (this.throwerName != null && this.throwerName.length() == 0)
        {
            this.throwerName = null;
        }

        smokeCountdown = par1NBTTagCompound.getByte("smokeCountdown");
        smokeLeft = par1NBTTagCompound.getByte("smokeLeft");
    }

    @SideOnly(Side.CLIENT)
    public float getShadowSize()
    {
        return 0.0F;
    }

    public EntityLiving getThrower()
    {
        if (this.thrower == null && this.throwerName != null && this.throwerName.length() > 0)
        {
            this.thrower = this.worldObj.getPlayerEntityByName(this.throwerName);
        }

        return this.thrower;
    }
}
