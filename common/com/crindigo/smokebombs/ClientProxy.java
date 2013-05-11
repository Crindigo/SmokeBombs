package com.crindigo.smokebombs;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import net.minecraft.client.renderer.entity.RenderSnowball;

/**
 * Created with IntelliJ IDEA.
 * User: steven
 * Date: 5/10/13
 * Time: 12:48 AM
 */
public class ClientProxy extends CommonProxy
{
    @Override
    public void registerRenderers()
    {
        super.registerRenderers();

        RenderingRegistry.registerEntityRenderingHandler(EntitySmokeBomb.class,
                new RenderSnowball(SmokeBombs.smokeBomb));
    }
}
