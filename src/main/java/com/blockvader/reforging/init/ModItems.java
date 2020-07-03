package com.blockvader.reforging.init;

import com.blockvader.reforging.ModItemTier;
import com.blockvader.reforging.Reforging;
import com.blockvader.reforging.item.HammerItem;
import com.blockvader.reforging.item.SpearItem;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemTier;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = Reforging.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModItems {
	
	public static Item CLUB;
	public static Item SPEAR;
	
	@SubscribeEvent
	public static void onItemRegistry(final RegistryEvent.Register<Item> event)
	{
		CLUB = new HammerItem(ItemTier.STONE, new Item.Properties().group(ItemGroup.COMBAT)).setRegistryName("club");
		event.getRegistry().register(CLUB);
		
		SPEAR = new SpearItem(ModItemTier.FLINT, new Item.Properties().group(ItemGroup.COMBAT)).setRegistryName("spear");
		event.getRegistry().register(SPEAR);
	}
}