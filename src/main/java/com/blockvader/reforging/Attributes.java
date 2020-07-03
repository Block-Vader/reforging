package com.blockvader.reforging;

import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class Attributes {
	
	public static final IAttribute ATTACK_REACH = new RangedAttribute(null, Reforging.MOD_ID + ".attack_reach", 3.0D, 0.0D, 1024.0D).setShouldWatch(true);
	public static final IAttribute ATTACK_KNOCKBACK = new RangedAttribute(null, Reforging.MOD_ID + ".attack_knockback", 0.4D, 0.0D, 1024.0D).setShouldWatch(true);
	
	public static final UUID ATTACK_REACH_MODIFIER = UUID.fromString("CD3F55D3-646C-4F38-A498-9413A36DB5C1");
	public static final UUID ATTACK_KNOCKBACK_MODIFIER = UUID.fromString("CB36EED3-1234-4F38-A497-9C13A33DB5CF");
	
	@SubscribeEvent
	public void onKnockback(LivingKnockBackEvent event)
	{
		if (event.getAttacker() instanceof PlayerEntity)
		{
			float f = (float) ((PlayerEntity)event.getAttacker()).getAttribute(ATTACK_KNOCKBACK).getValue();
			float f1 = event.getStrength() + f;
			event.setStrength(f1);
		}
	}
	
	@SubscribeEvent
	public void onPlayerSpawn(EntityJoinWorldEvent event)
	{
		Entity entity = event.getEntity();
		if (event.getEntity() instanceof PlayerEntity)
		{
			((PlayerEntity)entity).getAttributes().registerAttribute(ATTACK_REACH);
			((PlayerEntity)entity).getAttributes().registerAttribute(ATTACK_KNOCKBACK);
		}
	}
}
