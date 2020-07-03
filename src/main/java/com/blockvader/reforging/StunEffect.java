package com.blockvader.reforging;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

public class StunEffect extends Effect{

	public StunEffect(EffectType typeIn, int liquidColorIn) {
		super(typeIn, liquidColorIn);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void performEffect(LivingEntity entityLivingBaseIn, int amplifier)
	{
		if (entityLivingBaseIn instanceof MobEntity)
		{
			((MobEntity)entityLivingBaseIn).setAttackTarget(null);
			((MobEntity)entityLivingBaseIn).goalSelector.disableFlag(Goal.Flag.JUMP);
			((MobEntity)entityLivingBaseIn).goalSelector.disableFlag(Goal.Flag.LOOK);
			((MobEntity)entityLivingBaseIn).goalSelector.disableFlag(Goal.Flag.MOVE);
			((MobEntity)entityLivingBaseIn).goalSelector.disableFlag(Goal.Flag.TARGET);
		}
	}
	
	@Override
	public void removeAttributesModifiersFromEntity(LivingEntity entityLivingBaseIn, AbstractAttributeMap attributeMapIn, int amplifier)
	{
		if (entityLivingBaseIn instanceof MobEntity)
		{
			((MobEntity)entityLivingBaseIn).goalSelector.enableFlag(Goal.Flag.JUMP);
			((MobEntity)entityLivingBaseIn).goalSelector.enableFlag(Goal.Flag.LOOK);
			((MobEntity)entityLivingBaseIn).goalSelector.enableFlag(Goal.Flag.MOVE);
			((MobEntity)entityLivingBaseIn).goalSelector.enableFlag(Goal.Flag.TARGET);
		}
		super.removeAttributesModifiersFromEntity(entityLivingBaseIn, attributeMapIn, amplifier);
	}
	
	@Override
	public boolean isReady(int duration, int amplifier)
	{
		return true;
	}

}
