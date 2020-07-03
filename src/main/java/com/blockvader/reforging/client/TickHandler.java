package com.blockvader.reforging.client;

import com.blockvader.reforging.Reforging;
import com.blockvader.reforging.init.ModEffects;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(value = Dist.CLIENT, modid = Reforging.MOD_ID)
public class TickHandler {
	
	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event)
	{
		ClientPlayerEntity player = Minecraft.getInstance().player;
		if (player != null)
		{
			if (player.isPotionActive(ModEffects.STUN))
			{
				if (!(Minecraft.getInstance().currentScreen instanceof StunedScreen))
				{
					System.out.println(Minecraft.getInstance().currentScreen);
					Minecraft.getInstance().execute(() -> {
						Minecraft.getInstance().displayGuiScreen(new StunedScreen());
					});
				}
			}
		}
	}

}
