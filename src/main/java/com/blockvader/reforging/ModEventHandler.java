package com.blockvader.reforging;

import com.blockvader.reforging.init.ModEffects;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.boss.dragon.EnderDragonPartEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effects;
import net.minecraft.stats.Stats;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = Reforging.MOD_ID)
public class ModEventHandler {
	
	@SubscribeEvent
	public static void onEntityAttack(LivingAttackEvent event)
	{
		Entity targetEntity = event.getEntityLiving();
		
		if (targetEntity instanceof PlayerEntity && event.getSource().getImmediateSource() instanceof LivingEntity)
		{
			LivingEntity source = (LivingEntity) event.getSource().getImmediateSource();
			PlayerEntity playerentity = (PlayerEntity)targetEntity;
			ItemStack itemstack = source.getHeldItemMainhand();
			ItemStack itemstack1 = playerentity.isHandActive() ? playerentity.getActiveItemStack() : ItemStack.EMPTY;
			if (!itemstack.isEmpty() && !itemstack1.isEmpty() && itemstack.canDisableShield(itemstack1, playerentity, source) && itemstack1.isShield(playerentity))
			{
				int i = source.getHeldItemMainhand().getTag().contains("ShieldBreak") ? source.getHeldItemMainhand().getTag().getInt("ShieldBreak") : 30;
				{
					System.out.println(i);
					playerentity.getCooldownTracker().setCooldown(itemstack1.getItem(), i);
					source.world.setEntityState(playerentity, (byte)30);
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void onEntityJumped(LivingJumpEvent event)
	{
		if (event.getEntityLiving().isPotionActive(ModEffects.STUN))
		{
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public static void onPlayerAttack(AttackEntityEvent event)
	{
		Entity targetEntity = event.getTarget();
		
		if (event.getEntity() instanceof PlayerEntity)
		{
			PlayerEntity player = (PlayerEntity) event.getEntity();
			/*
			 * A custom attack logic when a sword attack happens
			 * Copy of net.minecraft.entity.player.PlayerEntity.attackTargetEntityWithCurrentItem(Entity targetEntity) with small changes
			 * Allows me to mess with sweep attack
			 * */
			if (player.getHeldItemMainhand().getItem() instanceof SwordItem  && player.getHeldItemMainhand().getTag().contains("SweepArea"))
			{
				if (targetEntity.canBeAttackedWithItem())
				{
					if (!targetEntity.hitByEntity(player))
					{
						float f = (float)player.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue();
						float f1;
						if (targetEntity instanceof LivingEntity)
						{
							f1 = EnchantmentHelper.getModifierForCreature(player.getHeldItemMainhand(), ((LivingEntity)targetEntity).getCreatureAttribute());
						} else
						{
							f1 = EnchantmentHelper.getModifierForCreature(player.getHeldItemMainhand(), CreatureAttribute.UNDEFINED);
						}

						float f2 = player.getCooledAttackStrength(0.5F);
						f = f * (0.2F + f2 * f2 * 0.8F);
						f1 = f1 * f2;
						player.resetCooldown();
						if (f > 0.0F || f1 > 0.0F)
						{
							boolean flag = f2 > 0.9F;
							boolean flag1 = false;
							int i = 0;
							i = i + EnchantmentHelper.getKnockbackModifier(player);
							if (player.isSprinting() && flag)
							{
								player.world.playSound((PlayerEntity)null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK, player.getSoundCategory(), 1.0F, 1.0F);
								++i;
								flag1 = true;
							}

							boolean flag2 = flag && player.fallDistance > 0.0F && !player.onGround && !player.isOnLadder() && !player.isInWater() && !player.isPotionActive(Effects.BLINDNESS) && !player.isPassenger() && targetEntity instanceof LivingEntity;
							flag2 = flag2 && !player.isSprinting();
							net.minecraftforge.event.entity.player.CriticalHitEvent hitResult = net.minecraftforge.common.ForgeHooks.getCriticalHit(player, targetEntity, flag2, flag2 ? 1.5F : 1.0F);
							flag2 = hitResult != null;
							if (flag2)
							{
								f *= hitResult.getDamageModifier();
							}

							f = f + f1;
							boolean flag3 = false;
							double d0 = (double)(player.distanceWalkedModified - player.prevDistanceWalkedModified);
							if (flag && !flag2 && !flag1 && player.onGround && d0 < (double)player.getAIMoveSpeed()) {
								ItemStack itemstack = player.getHeldItem(Hand.MAIN_HAND);
								if (itemstack.getItem() instanceof SwordItem) {
									flag3 = true;
								}
							}

							float f4 = 0.0F;
							boolean flag4 = false;
							int j = EnchantmentHelper.getFireAspectModifier(player);
							if (targetEntity instanceof LivingEntity) {
								f4 = ((LivingEntity)targetEntity).getHealth();
								if (j > 0 && !targetEntity.isBurning()) {
									flag4 = true;
									targetEntity.setFire(1);
								}
							}

							Vec3d vec3d = targetEntity.getMotion();
							boolean flag5 = targetEntity.attackEntityFrom(DamageSource.causePlayerDamage(player), f);
							if (flag5)
							{
								if (i > 0)
								{
									if (targetEntity instanceof LivingEntity) {
										((LivingEntity)targetEntity).knockBack(player, (float)i * 0.5F, (double)MathHelper.sin(player.rotationYaw * ((float)Math.PI / 180F)), (double)(-MathHelper.cos(player.rotationYaw * ((float)Math.PI / 180F))));
									} else {
										targetEntity.addVelocity((double)(-MathHelper.sin(player.rotationYaw * ((float)Math.PI / 180F)) * (float)i * 0.5F), 0.1D, (double)(MathHelper.cos(player.rotationYaw * ((float)Math.PI / 180F)) * (float)i * 0.5F));
									}

									player.setMotion(player.getMotion().mul(0.6D, 1.0D, 0.6D));
									player.setSprinting(false);
								}
								
								if (flag3)
								{
									float f3 = 1.0F + EnchantmentHelper.getSweepingDamageRatio(player) * f;
									double d = player.getHeldItemMainhand().getTag().getDouble("SweepArea");

									for(LivingEntity livingentity : player.world.getEntitiesWithinAABB(LivingEntity.class, targetEntity.getBoundingBox().grow(d, 0.25D, d)))
									{
										if (livingentity != player && livingentity != targetEntity && !player.isOnSameTeam(livingentity) && (!(livingentity instanceof ArmorStandEntity) || !((ArmorStandEntity)livingentity).hasMarker()) && player.getDistanceSq(livingentity) < 9.0D)
										{
											livingentity.knockBack(player, 0.4F, (double)MathHelper.sin(player.rotationYaw * ((float)Math.PI / 180F)), (double)(-MathHelper.cos(player.rotationYaw * ((float)Math.PI / 180F))));
											livingentity.attackEntityFrom(DamageSource.causePlayerDamage(player), f3);
										}
									}

									player.world.playSound((PlayerEntity)null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, player.getSoundCategory(), 1.0F, 1.0F);
									player.spawnSweepParticles();
								}

								if (targetEntity instanceof ServerPlayerEntity && targetEntity.velocityChanged)
								{
									((ServerPlayerEntity)targetEntity).connection.sendPacket(new SEntityVelocityPacket(targetEntity));
									targetEntity.velocityChanged = false;
									targetEntity.setMotion(vec3d);
								}

								if (flag2)
								{
									player.world.playSound((PlayerEntity)null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, player.getSoundCategory(), 1.0F, 1.0F);
									player.onCriticalHit(targetEntity);
								}

								if (!flag2 && !flag3)
								{
									if (flag)
									{
										player.world.playSound((PlayerEntity)null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, player.getSoundCategory(), 1.0F, 1.0F);
									} else {
										player.world.playSound((PlayerEntity)null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, player.getSoundCategory(), 1.0F, 1.0F);
									}
								}

								if (f1 > 0.0F)
								{
									player.onEnchantmentCritical(targetEntity);
								}

								player.setLastAttackedEntity(targetEntity);
								if (targetEntity instanceof LivingEntity)
								{
									EnchantmentHelper.applyThornEnchantments((LivingEntity)targetEntity, player);
								}

								EnchantmentHelper.applyArthropodEnchantments(player, targetEntity);
								ItemStack itemstack1 = player.getHeldItemMainhand();
								Entity entity = targetEntity;
								if (targetEntity instanceof EnderDragonPartEntity)
								{
									entity = ((EnderDragonPartEntity)targetEntity).dragon;
								}

								if (!player.world.isRemote && !itemstack1.isEmpty() && entity instanceof LivingEntity)
								{
									ItemStack copy = itemstack1.copy();
									itemstack1.hitEntity((LivingEntity)entity, player);
									if (itemstack1.isEmpty()) {
										net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, copy, Hand.MAIN_HAND);
										player.setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
									}
								}

								if (targetEntity instanceof LivingEntity)
								{
									float f5 = f4 - ((LivingEntity)targetEntity).getHealth();
									player.addStat(Stats.DAMAGE_DEALT, Math.round(f5 * 10.0F));
									if (j > 0) {
										targetEntity.setFire(j * 4);
									}

									if (player.world instanceof ServerWorld && f5 > 2.0F)
									{
										int k = (int)((double)f5 * 0.5D);
										((ServerWorld)player.world).spawnParticle(ParticleTypes.DAMAGE_INDICATOR, targetEntity.getPosX(), targetEntity.getPosYHeight(0.5D), targetEntity.getPosZ(), k, 0.1D, 0.0D, 0.1D, 0.2D);
									}
								}

								player.addExhaustion(0.1F);
							} else {
								player.world.playSound((PlayerEntity)null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, player.getSoundCategory(), 1.0F, 1.0F);
								if (flag4)
								{
									targetEntity.extinguish();
								}
							}
						}
					}
				}
				event.setCanceled(true);
			}
		}
	}
	
	public static void noTargetSweep(PlayerEntity player, double x, double y, double z)
	{
		float f = (float)player.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue();
		float f2 = player.getCooledAttackStrength(0.5F);
		f = f * (0.2F + f2 * f2 * 0.8F);
		player.resetCooldown();
		if (f > 0.0F)
		{
			boolean flag = f2 > 0.9F;
			
			boolean flag1 = false;
			if (player.isSprinting() && flag)
			{
				player.world.playSound((PlayerEntity)null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK, player.getSoundCategory(), 1.0F, 1.0F);
				flag1 = true;
			}
			
			boolean flag2 = flag && player.fallDistance > 0.0F && !player.onGround && !player.isOnLadder() && !player.isInWater() && !player.isPotionActive(Effects.BLINDNESS) && !player.isPassenger();
			
 			boolean flag3 = false;
			double d1 = (double)(player.distanceWalkedModified - player.prevDistanceWalkedModified);
			if (flag && !flag2 && !flag1 && player.onGround && d1 < (double)player.getAIMoveSpeed())
			{
				ItemStack itemstack = player.getHeldItem(Hand.MAIN_HAND);
				if (itemstack.getItem() instanceof SwordItem) {
					flag3 = true;
				}
			}
			
			if (flag3)
			{
				float f3 = 1.0F + EnchantmentHelper.getSweepingDamageRatio(player) * f;
				double d = player.getHeldItemMainhand().getTag().getDouble("SweepArea");
				System.out.println("Sweeping area");
				for(LivingEntity livingentity : player.world.getEntitiesWithinAABB(LivingEntity.class, player.getBoundingBox().grow(d)))
				{
					System.out.println("Entity found");
					if (livingentity != player && !player.isOnSameTeam(livingentity) && (!(livingentity instanceof ArmorStandEntity) || !((ArmorStandEntity)livingentity).hasMarker()) && player.getDistanceSq(livingentity) < 9.0D)
					{
						livingentity.knockBack(player, 0.4F, (double)MathHelper.sin(player.rotationYaw * ((float)Math.PI / 180F)), (double)(-MathHelper.cos(player.rotationYaw * ((float)Math.PI / 180F))));
						livingentity.attackEntityFrom(DamageSource.causePlayerDamage(player), f3);
					}
				}

				player.world.playSound((PlayerEntity)null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, player.getSoundCategory(), 1.0F, 1.0F);
				player.spawnSweepParticles();
			}
		}
	}
}
