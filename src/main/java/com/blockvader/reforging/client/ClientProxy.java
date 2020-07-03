package com.blockvader.reforging.client;

import com.blockvader.reforging.init.ModBlocks;
import com.blockvader.reforging.init.ModEntities;
import com.blockvader.reforging.init.ModItems;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy {
	
	@SuppressWarnings("deprecation")
	public static void registerItemModels()
	{
		registerModel(ModItems.CLUB);
		registerModel(ModItems.SPEAR);
		registerModel(Item.getItemFromBlock(ModBlocks.REFORGING_TABLE));
	}
	
	public static void registerEntityModels()
	{
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.SHOCKWAVE, ShockwaveRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.SPEAR, SpearRenderer::new);
	}
	
	private static void registerModel(Item item)
	{
		Minecraft.getInstance().getItemRenderer().getItemModelMesher().register(item, new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}
}