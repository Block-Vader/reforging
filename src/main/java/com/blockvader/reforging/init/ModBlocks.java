package com.blockvader.reforging.init;

import com.blockvader.reforging.Reforging;
import com.blockvader.reforging.ReforgingTableBlock;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = Reforging.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModBlocks {
	
	public static Block REFORGING_TABLE;
	
	@SubscribeEvent
	public static void onBlocksRegistry(final RegistryEvent.Register<Block> event)
	{
		REFORGING_TABLE = new ReforgingTableBlock(Block.Properties.create(Material.WOOD).hardnessAndResistance(2.5F).sound(SoundType.WOOD)).setRegistryName("reforging_table");
		event.getRegistry().register(REFORGING_TABLE);
	}
	
	@SubscribeEvent
	public static void onItemRegistry(final RegistryEvent.Register<Item> event)
	{
		event.getRegistry().register(new BlockItem(REFORGING_TABLE, new Item.Properties().group(ItemGroup.DECORATIONS)).setRegistryName("reforging_table"));
	}
}
