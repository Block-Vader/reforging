package com.blockvader.reforging.item;

import com.blockvader.reforging.ShockwaveEntity;
import com.blockvader.reforging.init.ModEntities;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class HammerItem extends WeaponItem {

	public HammerItem(IItemTier tierIn, Properties builder)
	{
		super(tierIn, 1.2F, -3.2F, 0.4F, 0.0F, builder);
		this.addPropertyOverride(new ResourceLocation("pull"), (p_210310_0_, p_210310_1_, p_210310_2_) -> {
			if (p_210310_2_ == null)
			{
				return 0.0F;
			} else
			{
				return !(p_210310_2_.getActiveItemStack().getItem() instanceof BowItem) ? 0.0F : (float)(p_210310_0_.getUseDuration() - p_210310_2_.getItemInUseCount()) / 20.0F;
			}
		});
		this.addPropertyOverride(new ResourceLocation("pulling"), (p_210309_0_, p_210309_1_, p_210309_2_) -> {
			return p_210309_2_ != null && p_210309_2_.isHandActive() && p_210309_2_.getActiveItemStack() == p_210309_0_ ? 1.0F : 0.0F;
		});
	}
	
	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft)
	{
		if (entityLiving instanceof PlayerEntity)
		{
			PlayerEntity playerentity = (PlayerEntity)entityLiving;
			{
				if (this.getUseDuration(stack) - timeLeft >= 20 && playerentity.onGround)
				{
					playerentity.getCooldownTracker().setCooldown(this, 200);
					if (!worldIn.isRemote)
					{
						ShockwaveEntity shockwave = new ShockwaveEntity(ModEntities.SHOCKWAVE, worldIn);
						shockwave.setPosition(playerentity.getPosX(), playerentity.getPosY(), playerentity.getPosZ());
						shockwave.setReach(7);
						shockwave.setDamage(4);
						shockwave.setDistanceTraveled(1.5F);
						stack.damageItem(1, playerentity, (p_220009_1_) -> {
							p_220009_1_.sendBreakAnimation(playerentity.getActiveHand());
						});
						worldIn.addEntity(shockwave);
					}
					worldIn.playSound((PlayerEntity)null, playerentity.getPosX(), playerentity.getPosY(), playerentity.getPosZ(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 1.0F, 1.0F);
				}
			}
		}
	}
	
	@Override
	public int getUseDuration(ItemStack stack)
	{
		return 72000;
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn)
	{
		ItemStack itemstack = playerIn.getHeldItem(handIn);
		playerIn.setActiveHand(handIn);
        return ActionResult.resultConsume(itemstack);
	}
	
	@Override
	public UseAction getUseAction(ItemStack stack)
	{
		return UseAction.BOW;
	}

}