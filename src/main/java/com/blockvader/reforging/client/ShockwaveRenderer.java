package com.blockvader.reforging.client;

import javax.annotation.Nonnull;

import com.blockvader.reforging.ShockwaveEntity;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class ShockwaveRenderer extends EntityRenderer<ShockwaveEntity>{

	public ShockwaveRenderer(EntityRendererManager renderManager)
	{
		super(renderManager);
	}

	@Override
	public ResourceLocation getEntityTexture(@Nonnull ShockwaveEntity entity) 
	{
		return null;
	}
}