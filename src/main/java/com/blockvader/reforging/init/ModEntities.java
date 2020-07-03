package com.blockvader.reforging.init;

import com.blockvader.reforging.Reforging;
import com.blockvader.reforging.ShockwaveEntity;
import com.blockvader.reforging.SpearEntity;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = Reforging.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModEntities {
	
	public static EntityType<ShockwaveEntity> SHOCKWAVE;
	public static EntityType<SpearEntity> SPEAR;
	
	@SubscribeEvent
	public static void onEntityTypeRegistry(RegistryEvent.Register<EntityType<?>> event)
	{
		SHOCKWAVE = EntityType.Builder.create(ShockwaveEntity::new, EntityClassification.MISC).size(1, 0.5F).build(Reforging.MOD_ID + "shockwave");
		SHOCKWAVE.setRegistryName("shockwave");
		event.getRegistry().register(SHOCKWAVE);
		SPEAR = EntityType.Builder.create(SpearEntity::new, EntityClassification.MISC).size(0.5F, 0.5F).build(Reforging.MOD_ID + "spear");
		SPEAR.setRegistryName("spear");
		event.getRegistry().register(SPEAR);
	}

}
