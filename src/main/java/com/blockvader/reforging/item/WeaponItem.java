package com.blockvader.reforging.item;

import com.blockvader.reforging.Attributes;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TieredItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WeaponItem extends TieredItem {
	
	private float attackDamage;
	private float attackSpeed;
	private float attackKnockback;
	private float attackReach;

	public WeaponItem(IItemTier tierIn, float damageMultiplyer, float speed, float knockback, float reach, Properties builder) {
		super(tierIn, builder);
		this.attackDamage = (3.0F + tierIn.getAttackDamage()) * damageMultiplyer;
		this.attackSpeed = speed;
		this.attackKnockback = knockback;
		this.attackReach = reach;
	}
	
	public boolean canPlayerBreakBlockWhileHolding(BlockState state, World worldIn, BlockPos pos, PlayerEntity player)
	{
		return !player.isCreative();
	}
	
	public float getAttackDamage() {
		return attackDamage;
	}
	
	@Override
	public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker)
	{
		stack.damageItem(1, attacker, (p_220045_0_) -> {
			p_220045_0_.sendBreakAnimation(EquipmentSlotType.MAINHAND);
		});
		return true;
	}
	
	@Override
	public boolean onBlockDestroyed(ItemStack stack, World worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving)
	{
		if (state.getBlockHardness(worldIn, pos) != 0.0F)
		{
			stack.damageItem(2, entityLiving, (p_220044_0_) -> {
				p_220044_0_.sendBreakAnimation(EquipmentSlotType.MAINHAND);
			});
		}
		return true;
	}
	
	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot)
	{
		Multimap<String, AttributeModifier> multimap = HashMultimap.create();
		if (equipmentSlot == EquipmentSlotType.MAINHAND)
		{
			multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", (double)this.attackDamage, AttributeModifier.Operation.ADDITION));
			multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", (double)this.attackSpeed, AttributeModifier.Operation.ADDITION));
			multimap.put(Attributes.ATTACK_KNOCKBACK.getName(), new AttributeModifier(Attributes.ATTACK_KNOCKBACK_MODIFIER, "Weapon modifier", (double)this.attackKnockback, AttributeModifier.Operation.ADDITION));
			multimap.put(Attributes.ATTACK_REACH.getName(), new AttributeModifier(Attributes.ATTACK_REACH_MODIFIER, "Weapon modifier", (double)this.attackReach, AttributeModifier.Operation.ADDITION));
		}

		return multimap;
	}

}
