package com.blockvader.reforging;

import java.util.Iterator;
import java.util.UUID;

import com.blockvader.reforging.init.ModContainers;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.TridentItem;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ReforgingContainer extends Container{

	private double[] baseStats = new double[]{0, 0, 0, 0, 0};
	
	//Client only values used by user interface
	private double[] stats = new double[]{0, 0, 0, 0, 0};
	private byte[] statChanges = new byte[]{0, 0, 0, 0, 0};
	private int ups;
	private int downs;
	
	public static final UUID DAMAGE_ID = UUID.fromString("9c7a6f3e-ac20-11ea-bb37-0242ac130002");
	public static final UUID SPEED_ID = UUID.fromString("9c7a7182-ac20-11ea-bb37-0242ac130002");
	public static final UUID KNOCKBACK_ID = UUID.fromString("9c7a7272-ac20-11ea-bb37-0242ac130002");
	public static final UUID REACH_ID = UUID.fromString("9c7a7344-ac20-11ea-bb37-0242ac130002");
	
	protected ReforgingContainer(ContainerType<?> type, int id)
	{
		super(type, id);
	}
	
	public ReforgingContainer(int windowId, PlayerInventory inventory, PacketBuffer buff)
	{
		this(windowId, new ItemStackHandler(2), inventory);
	}
	
	public ReforgingContainer(int windowId, IItemHandler handler, PlayerInventory inventory)
	{
		this(ModContainers.REFORGING_MENU, windowId);
		this.addSlots(handler, inventory, this);
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn)
	{
		return true;//isWithinUsableDistance(worldPos, playerIn, ModBlocks.REFORGING_TABLE);
	}
	
	private void addSlots(IItemHandler handler, PlayerInventory inventory, ReforgingContainer container)
	{
		this.addSlot(new SlotItemHandler(handler, 0, 143, 36){
			
			@Override
			public void onSlotChanged() {

				container.setBaseStats();
				container.statChanges = new byte[]{0, 0, 0, 0, 0};
				container.ups = 0;
				container.downs = 0;
				super.onSlotChanged();
			}
			
			@Override
			public boolean isItemValid(ItemStack stack) {
				if (stack.getItem() instanceof SwordItem || stack.getItem() instanceof AxeItem || stack.getItem() instanceof TridentItem) 
				{
					return (!stack.getTag().getBoolean("Reforged"));
				}
				return super.isItemValid(stack);
			}
			
		});
		this.addSlot(new SlotItemHandler(handler, 1, 143, 90){
			
			@Override
			public boolean isItemValid(ItemStack stack) {
				return !stack.getItem().isDamageable(); //Accepts material only, not gear
			}
			
		});
		
		for (int k = 0; k < 3; ++k)
        {
            for (int i1 = 0; i1 < 9; ++i1)
            {
                this.addSlot(new Slot(inventory, i1 + k * 9 + 9, 8 + i1 * 18, 138 + k * 18));
            }
        }

        for (int l = 0; l < 9; ++l)
        {
            this.addSlot(new Slot(inventory, l, 8 + l * 18, 196));
        }
	}
	
	@Override
	public ItemStack transferStackInSlot(final PlayerEntity player, final int index)
	{
		ItemStack itemstack = ItemStack.EMPTY;
		final Slot slot = this.inventorySlots.get(index);
		if ((slot != null) && slot.getHasStack())
		{
			final ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			final int containerSlots = this.inventorySlots.size() - player.inventory.mainInventory.size();
			if (index < containerSlots)
			{
				if (!mergeItemStack(itemstack1, containerSlots, this.inventorySlots.size(), true))
				{
					return ItemStack.EMPTY;
				}
			}
			else if (!mergeItemStack(itemstack1, 0, containerSlots, false))
			{
				return ItemStack.EMPTY;
			}
			if (itemstack1.getCount() == 0)
			{
				slot.putStack(ItemStack.EMPTY);
			}
			else
			{
				slot.onSlotChanged();
			}
			if (itemstack1.getCount() == itemstack.getCount())
			{
				return ItemStack.EMPTY;
			}
			slot.onTake(player, itemstack1);
		}
		return itemstack;
	}
	
	public double[] getStats()
	{
		return this.stats;
	}
	
	public byte[] getStatChanges()
	{
		return this.statChanges;
	}
	
	public boolean canReforge()
	{
		if (!this.getSlot(0).getStack().isEmpty())
		{
			Item item = this.getSlot(0).getStack().getItem();
			if (item instanceof SwordItem || item instanceof AxeItem || item instanceof TridentItem) 
			{
				return (!this.getSlot(0).getStack().getTag().getBoolean("Reforged"));
			}
		}
		return false;
	}
	
	@Override
	public void onContainerClosed(PlayerEntity playerIn) {
		if (!playerIn.isAlive() || playerIn instanceof ServerPlayerEntity && ((ServerPlayerEntity)playerIn).hasDisconnected()) {
	         for(int j = 0; j < 2; ++j) {
	            playerIn.dropItem(this.getSlot(j).getStack(), false);
	            this.getSlot(j).putStack(ItemStack.EMPTY);
	         }

	      } else {
	         for(int i = 0; i < 2; ++i) {
	            playerIn.inventory.placeItemBackInInventory(playerIn.getEntityWorld(), this.getSlot(i).getStack());
	            this.getSlot(i).putStack(ItemStack.EMPTY);
	         }

	      }
		super.onContainerClosed(playerIn);
	}
	
	private void setBaseStats()
	{
		double dmg = 1;
		double spd = 4;
		double knockback = 0;
		double reach = 0;
		ItemStack stack = this.getSlot(0).getStack();
		Iterator<AttributeModifier> dmgItr = stack.getAttributeModifiers(EquipmentSlotType.MAINHAND).get(SharedMonsterAttributes.ATTACK_DAMAGE.getName()).iterator();
		while (dmgItr.hasNext())
		{
			AttributeModifier mod = dmgItr.next();
			if (mod.getOperation() == Operation.ADDITION)
			{
				dmg += mod.getAmount();
			}
		}
		this.baseStats[0] = dmg;
		Iterator<AttributeModifier> spdItr = stack.getAttributeModifiers(EquipmentSlotType.MAINHAND).get(SharedMonsterAttributes.ATTACK_SPEED.getName()).iterator();
		while (spdItr.hasNext())
		{
			AttributeModifier mod = spdItr.next();
			if (mod.getOperation() == Operation.ADDITION)
			{
				spd += mod.getAmount();
			}
		}
		this.baseStats[1] = spd;
		Iterator<AttributeModifier> knockbackItr = stack.getAttributeModifiers(EquipmentSlotType.MAINHAND).get(Attributes.ATTACK_KNOCKBACK.getName()).iterator();
		if (!knockbackItr.hasNext()) knockback = 0.4d; //Gives knockback attribute default value since vanilla weapons don't have it
		while (knockbackItr.hasNext())
		{
			AttributeModifier mod = knockbackItr.next();
			if (mod.getOperation() == Operation.ADDITION)
			{
				knockback += mod.getAmount();
			}
		}
		this.baseStats[2] = knockback;
		Iterator<AttributeModifier> reachItr = stack.getAttributeModifiers(EquipmentSlotType.MAINHAND).get(Attributes.ATTACK_REACH.getName()).iterator();
		if (!reachItr.hasNext()) reach = 3.0d; //Gives reach attribute default value since vanilla weapons don't have it
		while (reachItr.hasNext())
		{
			AttributeModifier mod = reachItr.next();
			if (mod.getOperation() == Operation.ADDITION)
			{
				reach += mod.getAmount();
			}
		}
		this.baseStats[3] = reach;
		if (this.getSlot(0).getStack().getItem() instanceof SwordItem) this.baseStats[4] = 1.0D;
		else if (this.getSlot(0).getStack().getItem() instanceof AxeItem) this.baseStats[4] = 1.5D;
		this.stats = this.baseStats.clone();
	}
	
	public boolean canUp(int i)
	{
		if (i < 0 || i > 5 || this.ups >= this.downs || this.ups > 10) return false;
		return statChanges[i] < 5;
	}
	
	public boolean canDown(int i)
	{
		if (i < 0 || i > 5 || this.downs > 10) return false;
		return this.statChanges[i] > -5;
	}
	
	public void statUp(int i)
	{
		if (i < 0 || i > 5 ) return;
		double d = 0.0D;
		if (i == 0) d = baseStats[0] / 20.0D;
		if (i == 1) d = baseStats[1] / 15.0D;
		if (i == 2) d = 0.1;
		if (i == 3) d = 0.1;
		if (i == 4)
		{
			if (this.getSlot(0).getStack().getItem() instanceof SwordItem) d = 0.2D;
			else if (this.getSlot(0).getStack().getItem() instanceof AxeItem) d = 0.3D;
		}
		if (this.statChanges[i] >= 0) this.ups += 1;
		else this.downs -= 1;
		this.stats[i] += d;
		this.statChanges[i] += 1;
	}
	
	public void statDown(int i)
	{
		if (i < 0 || i > 5 ) return;
		double d = 0.0D;
		if (i == 0) d = baseStats[0] / 20.0D;
		if (i == 1) d = baseStats[1] / 15.0D;
		if (i == 2) d = 0.1;
		if (i == 3) d = 0.1;
		if (i == 4)
		{
			if (this.getSlot(0).getStack().getItem() instanceof SwordItem) d = 0.2D;
			else if (this.getSlot(0).getStack().getItem() instanceof AxeItem) d = 0.3D;
		}
		if (this.statChanges[i] <= 0) this.downs += 1;
		else this.ups -= 1;
		this.stats[i] -= d;
		this.statChanges[i] -= 1;
	}
	
	public boolean enoughResources()
	{
		if (this.getSlot(0).getStack().getItem().getIsRepairable(this.getSlot(0).getStack(), this.getSlot(1).getStack()))
		{
			return (this.getSlot(1).getStack().getCount() >= (this.ups * (this.ups + 1)) / 2);
		}
		return false;
	}
	
	public int getUps()
	{
		return this.ups;
	}
	
	public int getDowns()
	{
		return this.downs;
	}
	
	public void reforge(byte[] changes)
	{
		//Server side safety check
		if (!this.canReforge()) return;
		for (int j = 0; j < 5; j++)
		{
			if (Math.abs(changes[j]) > 5) return; //Single stat can be lowered/raised 5 times only
		}
		int i = Math.max(0, changes[0]) + Math.max(0, changes[1]) + Math.max(0, changes[2]) + Math.max(0, changes[3]) + Math.max(0, changes[4]);
		if ((changes[0] + changes[1] + changes[2] + changes[3] + changes[4] == 0) && i <= 10) //Stats can be changed 10 times only, same number of stat ups as stat downs
		{
			if (!this.getSlot(1).getStack().isEmpty())
			{
				if (this.getSlot(0).getStack().getItem().getIsRepairable(this.getSlot(0).getStack(), this.getSlot(1).getStack()))
				{
					if (this.getSlot(1).getStack().getCount() >= (i * (i + 1)) / 2); //Check for enough resources
					{
						//Actual reforge logic
						ItemStack stack = this.getSlot(0).getStack();
						Iterator<AttributeModifier> dmgItr = stack.getAttributeModifiers(EquipmentSlotType.MAINHAND).get(SharedMonsterAttributes.ATTACK_DAMAGE.getName()).iterator();
						while (dmgItr.hasNext())
						{
							AttributeModifier mod = dmgItr.next();
							if (mod.getOperation() == Operation.ADDITION)
							{
								dmgItr.remove();
							}
						}
						double dmg = this.baseStats[0] - 1.0D + changes[0] * (this.baseStats[0] / 20.0D);
						stack.addAttributeModifier(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(DAMAGE_ID, "Reforged damage", dmg, Operation.ADDITION), EquipmentSlotType.MAINHAND);
						
						Iterator<AttributeModifier> spdItr = stack.getAttributeModifiers(EquipmentSlotType.MAINHAND).get(SharedMonsterAttributes.ATTACK_SPEED.getName()).iterator();
						while (spdItr.hasNext())
						{
							AttributeModifier mod = spdItr.next();
							if (mod.getOperation() == Operation.ADDITION)
							{
								spdItr.remove();
							}
						}
						double spd = this.baseStats[1] - 4.0D + changes[1] * (this.baseStats[1] / 15.0D);
						stack.addAttributeModifier(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(SPEED_ID, "Reforged speed", spd, Operation.ADDITION), EquipmentSlotType.MAINHAND);
						
						Iterator<AttributeModifier> knockbackItr = stack.getAttributeModifiers(EquipmentSlotType.MAINHAND).get(Attributes.ATTACK_KNOCKBACK.getName()).iterator();
						while (knockbackItr.hasNext())
						{
							AttributeModifier mod = knockbackItr.next();
							if (mod.getOperation() == Operation.ADDITION)
							{
								knockbackItr.remove();
							}
						}
						double knockback = this.baseStats[2] + changes[3] * 0.1D;
						stack.addAttributeModifier(Attributes.ATTACK_KNOCKBACK.getName(), new AttributeModifier(KNOCKBACK_ID, "Reforged knockback", knockback, Operation.ADDITION), EquipmentSlotType.MAINHAND);
						
						Iterator<AttributeModifier> reachItr = stack.getAttributeModifiers(EquipmentSlotType.MAINHAND).get(Attributes.ATTACK_REACH.getName()).iterator();
						while (reachItr.hasNext())
						{
							AttributeModifier mod = reachItr.next();
							if (mod.getOperation() == Operation.ADDITION)
							{
								reachItr.remove();
							}
						}
						double reach = this.baseStats[3] + changes[3] * 0.1D;
						stack.addAttributeModifier(Attributes.ATTACK_REACH.getName(), new AttributeModifier(REACH_ID, "Reforged reach", reach, Operation.ADDITION), EquipmentSlotType.MAINHAND);
						if (stack.getItem() instanceof SwordItem && changes[4] != 1.0D) stack.getTag().putDouble("SweepArea", 1.0D + 0.2D * changes[4]);
						else if (stack.getItem() instanceof AxeItem && changes[4] != 1.5D) stack.getTag().putDouble("ShieldBreak", 30.0D + 6.0D * changes[4]);
						stack.getTag().putBoolean("Reforged", true);
						this.getSlot(1).getStack().shrink((i * (i + 1)) / 2);
					}
				}
			}
		}
	}
}
