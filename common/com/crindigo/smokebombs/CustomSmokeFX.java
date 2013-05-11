package com.crindigo.smokebombs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class CustomSmokeFX extends EntitySmokeFX
{
    public CustomSmokeFX(World par1World, double par2, double par4, double par6, double par8,
                         double par10, double par12) {
        super(par1World, par2, par4, par6, par8, par10, par12);
    }

    /**
     *
     * @param par1World world
     * @param par2 x pos
     * @param par4 y pos
     * @param par6 z pos
     * @param par8 x vel
     * @param par10 y vel
     * @param par12 z vel
     * @param par14 size
     */
    public CustomSmokeFX(World par1World, double par2, double par4, double par6, double par8,
                         double par10, double par12, float par14)
    {
        super(par1World, par2, par4, par6, par8, par10, par12, par14);

        this.particleScale *= 0.75F;
        this.particleScale *= par14;
    }

    public CustomSmokeFX(World world, double x, double y, double z, float r, float g, float b)
    {
        this(world, x, y, z, world.rand.nextDouble() * .14 - 0.07, 0.0, world.rand.nextDouble() * .14 - 0.07, 3.5f);

        this.particleRed = r;
        this.particleGreen = g;
        this.particleBlue = b;

        this.particleMaxAge = 80 + (int)(Math.random() * 80);
    }

    /**
     * Called to update the entity's position/logic.
     */
    @Override
    public void onUpdate()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge)
        {
            this.setDead();
        }

        this.setParticleTextureIndex(7 - this.particleAge * 8 / this.particleMaxAge);
        this.motionY += -0.003 + (.006 * (this.particleMaxAge - this.particleAge) / this.particleMaxAge);
        this.motionY = this.motionY < 0.0 ? 0.0 : this.motionY;

        this.moveEntity(this.motionX, this.motionY, this.motionZ);

        if (this.posY == this.prevPosY)
        {
            this.motionX *= 1.06;
            this.motionZ *= 1.06;
        }

        this.motionX *= 0.94;
        this.motionY *= 0.96;
        this.motionZ *= 0.94;

        if (this.onGround)
        {
            this.motionX *= 0.7;
            this.motionZ *= 0.7;
        }
    }
}
