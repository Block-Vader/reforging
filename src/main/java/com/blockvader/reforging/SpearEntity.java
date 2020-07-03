package com.blockvader.reforging;

import com.blockvader.reforging.init.ModItems;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;

public class SpearEntity extends AbstractArrowEntity {
	
	private static final DataParameter<Boolean> ENCHANTED = EntityDataManager.createKey(TridentEntity.class, DataSerializers.BOOLEAN);
	private ItemStack thrownStack = new ItemStack(ModItems.SPEAR);
	private boolean dealtDamage;

	public SpearEntity(EntityType<? extends AbstractArrowEntity> type, World worldIn) {
		super(type, worldIn);
	}

	@Override
	public ItemStack getArrowStack()
	{
		return this.thrownStack.copy();
	}
	
	public void setThrownStack(ItemStack stack)
	{
		this.thrownStack = stack.copy();
		if (stack.hasEffect()) this.dataManager.set(ENCHANTED, true);
	}
	
	public boolean isEnchanted()
	{
		return this.dataManager.get(ENCHANTED);
	}
	
	@Override
	protected void onEntityHit(EntityRayTraceResult p_213868_1_)
	{
		Entity entity = p_213868_1_.getEntity();
		float f = 8.0F;
		if (entity instanceof LivingEntity)
		{
			LivingEntity livingentity = (LivingEntity)entity;
			f += EnchantmentHelper.getModifierForCreature(this.thrownStack, livingentity.getCreatureAttribute());
		}

		Entity entity1 = this.getShooter();
		DamageSource damagesource = new IndirectEntityDamageSource("spear", this, (Entity)(entity1 == null ? this : entity1));
		this.dealtDamage = true;
		SoundEvent soundevent = SoundEvents.ITEM_TRIDENT_HIT;
		if (entity.attackEntityFrom(damagesource, f)) {
			if (entity.getType() == EntityType.ENDERMAN) {
				return;
			}

			if (entity instanceof LivingEntity) {
				LivingEntity livingentity1 = (LivingEntity)entity;
				if (entity1 instanceof LivingEntity)
				{
					EnchantmentHelper.applyThornEnchantments(livingentity1, entity1);
					EnchantmentHelper.applyArthropodEnchantments((LivingEntity)entity1, livingentity1);
				}

				this.arrowHit(livingentity1);
			}
		}

		this.setMotion(this.getMotion().mul(-0.01D, -0.1D, -0.01D));

		this.playSound(soundevent, 1.0F, 1.0F);
	}
	
	public void readAdditional(CompoundNBT compound)
	{
		super.readAdditional(compound);
		if (compound.contains("Spear", 10)) {
			this.thrownStack = ItemStack.read(compound.getCompound("Spear"));
		}
		this.dealtDamage = compound.getBoolean("DealtDamage");
	}

	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		System.out.println(this.thrownStack);
		compound.put("Spear", this.thrownStack.write(new CompoundNBT()));
		compound.putBoolean("DealtDamage", this.dealtDamage);
	}
	
	protected float getWaterDrag()
	{
		return 0.99F;
	}

}
