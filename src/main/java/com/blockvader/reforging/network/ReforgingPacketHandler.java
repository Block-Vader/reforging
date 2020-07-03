package com.blockvader.reforging.network;

import java.util.function.Supplier;

import com.blockvader.reforging.Attributes;
import com.blockvader.reforging.ModEventHandler;
import com.blockvader.reforging.Reforging;
import com.blockvader.reforging.ReforgingContainer;
import com.blockvader.reforging.init.ModContainers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class ReforgingPacketHandler {
	
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
	    new ResourceLocation(Reforging.MOD_ID, "main"),
	    () -> PROTOCOL_VERSION,
	    PROTOCOL_VERSION::equals,
	    PROTOCOL_VERSION::equals
	);
	private static int id;
	
	public static class ExtendedAttackPacket
	{
	    private final int entityId;
	    
	    ExtendedAttackPacket(PacketBuffer buf)
	    {
	        this.entityId = buf.readInt();
	    }

	    public ExtendedAttackPacket(int entityId)
	    {
	        this.entityId = entityId;
	    }

	    void encode(PacketBuffer buf)
	    {
	        buf.writeInt(entityId);
	    }

	    /**
	     * Copy of net.minecraft.network.play.ServerPlayNetHandler.processUseEntity(CUseEntityPacket packetIn) taking custom reach into account
	     */
	    void handle(Supplier<NetworkEvent.Context> ctx)
	    {
	 	ctx.get().enqueueWork(() -> {
	 		ServerPlayerEntity sender = ctx.get().getSender();
	 		ServerWorld serverworld = sender.getServerWorld();
	 		Entity entity = serverworld.getEntityByID(entityId);
	 		sender.markPlayerActive();
	 		if (entity != null)
	 		{
	 			boolean flag = sender.canEntityBeSeen(entity);
	 			double d0 = Math.pow(sender.getAttribute(Attributes.ATTACK_REACH).getValue() + 1.0D, 2);
	 			if (!flag)
	 			{
	 				d0 /= 4.0D;
	 			}

	 			if (sender.getDistanceSq(entity) < d0)
	 			{
 					if (entity instanceof ItemEntity || entity instanceof ExperienceOrbEntity || entity instanceof AbstractArrowEntity || entity == sender)
 					{
 						sender.connection.disconnect(new TranslationTextComponent("multisender.disconnect.invalid_entity_attacked"));
 						serverworld.getServer().logWarning("Player " + sender.getName().getString() + " tried to attack an invalid entity");
 						return;
 					}
 					sender.attackTargetEntityWithCurrentItem(entity);
	 			}
	 		}
	 	});
	 	ctx.get().setPacketHandled(true);
	    }
	}
	
	public static class ReforgePacket
	{
		private final int windowId;
		private final byte[] statChanges;
	    
		ReforgePacket(PacketBuffer buf)
	    {
	        this.windowId = buf.readInt();
	        this.statChanges = buf.readByteArray();
	    }

	    public ReforgePacket(int windowId, byte[] changes)
	    {
	        this.windowId = windowId;
	        this.statChanges = changes;
	    }

	    void encode(PacketBuffer buf)
	    {
	        buf.writeInt(windowId);
	        buf.writeByteArray(statChanges);
	    }
	    
	    void handle(Supplier<NetworkEvent.Context> ctx)
	    {

	    	ctx.get().enqueueWork(() -> {
	    		ServerPlayerEntity sender = ctx.get().getSender();
				sender.markPlayerActive();
				if (sender.openContainer.windowId == windowId && sender.openContainer.getCanCraft(sender) && !sender.isSpectator() && sender.openContainer.getType() == ModContainers.REFORGING_MENU)
				{
					if (statChanges.length == 5)
					{
						((ReforgingContainer)sender.openContainer).reforge(statChanges);
						sender.openContainer.detectAndSendChanges();
						sender.playSound(SoundEvents.BLOCK_ANVIL_USE, 1.0F, 1.0F);
					}
				}
		 	});
		 	ctx.get().setPacketHandled(true);
	    }
	}
	
	public static class NoTargetSweepPacket
	{
		private final double x;
		private final double y;
		private final double z;
		
		public NoTargetSweepPacket(PacketBuffer buf)
		{
			this.x = buf.readDouble();
			this.y = buf.readDouble();
			this.z = buf.readDouble();
		};
		
		public NoTargetSweepPacket(double x, double y, double z)
		{
			this.x = x;
			this.y = y;
			this.z = z;
		};
		
		void encode(PacketBuffer buf)
		{
			buf.writeDouble(x);
			buf.writeDouble(y);
			buf.writeDouble(z);
		};
		
		void handle(Supplier<NetworkEvent.Context> ctx)
	    {
			ctx.get().enqueueWork(() -> {
				
				ServerPlayerEntity sender = ctx.get().getSender();
		 		sender.markPlayerActive();
		 		double d0 = Math.pow(sender.getAttribute(Attributes.ATTACK_REACH).getValue() + 1.0D, 2);
		 		if (sender.getDistanceSq(x, y, z) < d0)
		 		{
		 			ModEventHandler.noTargetSweep(sender, x, y, z);
		 		}
			});
	    }
	}
	
	public static void register()
	{
		INSTANCE.registerMessage(id++, ExtendedAttackPacket.class, ExtendedAttackPacket::encode, ExtendedAttackPacket::new, ExtendedAttackPacket::handle);
		INSTANCE.registerMessage(id++, ReforgePacket.class, ReforgePacket::encode, ReforgePacket::new, ReforgePacket::handle);
		INSTANCE.registerMessage(id++, NoTargetSweepPacket.class, NoTargetSweepPacket::encode, NoTargetSweepPacket::new, NoTargetSweepPacket::handle);
	}

}