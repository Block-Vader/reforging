package com.blockvader.reforging.item;

import com.blockvader.reforging.SpearEntity;
import com.blockvader.reforging.init.ModEntities;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class SpearItem extends WeaponItem{

	public SpearItem(IItemTier tierIn, Properties builder) {
		super(tierIn, 1.0F, -3.0F, 0.0F, 0.5F, builder);
		this.addPropertyOverride(new ResourceLocation("throwing"), (p_210315_0_, p_210315_1_, p_210315_2_) -> {
	         return p_210315_2_ != null && p_210315_2_.isHandActive() && p_210315_2_.getActiveItemStack() == p_210315_0_ ? 1.0F : 0.0F;
	      });
	}
	
	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft)
	{
		if (entityLiving instanceof PlayerEntity) {
			PlayerEntity playerentity = (PlayerEntity)entityLiving;
			int i = this.getUseDuration(stack) - timeLeft;
			if (i >= 10)
			{
				if (!worldIn.isRemote)
				{
					stack.damageItem(1, playerentity, (p_220047_1_) -> {
						p_220047_1_.sendBreakAnimation(entityLiving.getActiveHand());
					});
					SpearEntity spear = new SpearEntity(ModEntities.SPEAR, worldIn);
					spear.setThrownStack(stack.copy());
					spear.setShooter(playerentity);
					spear.setPosition(playerentity.getPosX(), playerentity.getPosY(), playerentity.getPosZ());
					spear.shoot(playerentity, playerentity.rotationPitch, playerentity.rotationYaw, 0.0F, 2.5F, 1.0F);
					if (playerentity.abilities.isCreativeMode)
					{
						spear.pickupStatus = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
					}

					worldIn.addEntity(spear);
					System.out.println(spear);
					worldIn.playMovingSound((PlayerEntity)null, spear, SoundEvents.ITEM_TRIDENT_THROW, SoundCategory.PLAYERS, 1.0F, 1.0F);
					if (!playerentity.abilities.isCreativeMode)
					{
						playerentity.inventory.deleteStack(stack);
					}
				}
			}
			playerentity.addStat(Stats.ITEM_USED.get(this));

		}
	}
	
	public UseAction getUseAction(ItemStack stack)
	{
		return UseAction.SPEAR;
	}
	
	public int getUseDuration(ItemStack stack)
	{
		return 72000;
	}
	
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn)
	{
		ItemStack itemstack = playerIn.getHeldItem(handIn);
		if (itemstack.getDamage() >= itemstack.getMaxDamage() - 1)
		{
			return ActionResult.resultFail(itemstack);
		} else
		{
			playerIn.setActiveHand(handIn);
			return ActionResult.resultConsume(itemstack);
		}
	}

}
