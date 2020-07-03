package com.blockvader.reforging.init;

import com.blockvader.reforging.Reforging;
import com.blockvader.reforging.StunEffect;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = Reforging.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModEffects {
	
	public static Effect STUN;
	
	@SubscribeEvent
	public static void onEffectRegistry(RegistryEvent.Register<Effect> event)
	{
		STUN = new StunEffect(EffectType.HARMFUL, 16776960).setRegistryName(Reforging.MOD_ID, "stun");
		STUN.addAttributesModifier(SharedMonsterAttributes.MOVEMENT_SPEED, "5fb6534d-4d26-4aca-8133-d4f735507a7c", -1, Operation.MULTIPLY_TOTAL);
		event.getRegistry().register(STUN);
	}

}
