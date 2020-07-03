package com.blockvader.reforging.init;

import com.blockvader.reforging.Reforging;
import com.blockvader.reforging.ReforgingContainer;

import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = Reforging.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class ModContainers {
	
	public static ContainerType<ReforgingContainer> REFORGING_MENU;
	
	@SubscribeEvent
	public static void onContainerRegistry(RegistryEvent.Register<ContainerType<?>> event)
	{
		REFORGING_MENU = IForgeContainerType.create(ReforgingContainer::new);
		REFORGING_MENU.setRegistryName(Reforging.MOD_ID, "reforging_menu");
		event.getRegistry().register(REFORGING_MENU);
	}

}