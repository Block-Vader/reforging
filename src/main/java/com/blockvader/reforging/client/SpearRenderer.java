package com.blockvader.reforging.client;

import com.blockvader.reforging.SpearEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;

@SuppressWarnings("deprecation")
public class SpearRenderer extends EntityRenderer<SpearEntity>{

	protected SpearRenderer(EntityRendererManager renderManager) {
		super(renderManager);
	}
	
	@Override
	public void render(SpearEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrix, IRenderTypeBuffer bufferIn, int packedLightIn)
	{
		System.out.println("Trying to render");
		matrix.push();
		matrix.translate(0, 0.2, 0);
		
		Minecraft mc = Minecraft.getInstance();

		RenderSystem.enableBlend();
		mc.getItemRenderer().renderItem(entityIn.getArrowStack(), TransformType.FIXED, packedLightIn, OverlayTexture.NO_OVERLAY, matrix, bufferIn);
		
		matrix.pop();
	}

	@Override
	public ResourceLocation getEntityTexture(SpearEntity entity)
	{
		return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
	}

}
