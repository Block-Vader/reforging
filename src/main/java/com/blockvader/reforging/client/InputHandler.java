package com.blockvader.reforging.client;

import org.apache.logging.log4j.LogManager;

import com.blockvader.reforging.Attributes;
import com.blockvader.reforging.Reforging;
import com.blockvader.reforging.network.ReforgingPacketHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(value = Dist.CLIENT, modid = Reforging.MOD_ID)
public class InputHandler {
	
	@SubscribeEvent
	public static void onAttackInput(InputEvent.ClickInputEvent event)
	{
		ClientPlayerEntity player = Minecraft.getInstance().player;
		if (player != null)
		{
			if (event.isAttack())
			{
				double reach = player.getAttribute(Attributes.ATTACK_REACH).getValue();
				if (reach > 3.0)
				{
					if (clickMouse(reach - 1)) event.setCanceled(true);
				}
			}
		}
	}
	
	private static boolean clickMouse(double distance)
	{
		Minecraft mc = Minecraft.getInstance();
		RayTraceResult objectMouseOver = null;
		float partialTicks = mc.getRenderPartialTicks();
		Entity entity = mc.getRenderViewEntity();
		if (entity != null)
		{
			if (mc.world != null)
			{
				mc.getProfiler().startSection("pick");
				mc.pointedEntity = null;
				objectMouseOver = entity.pick(distance, partialTicks, false);
				Vec3d vec3d = entity.getEyePosition(partialTicks);
				boolean flag = false;
				double d1 = distance;
				if (mc.playerController.extendedReach())
				{
					d1 = 6.0D;
					distance = Math.max(d1, distance);
				} 
				else
				{
					if (distance > 3.0D)
					{
						//flag = true;
					}
				}

				d1 = d1 * d1;
				if (objectMouseOver != null)
				{
					d1 = objectMouseOver.getHitVec().squareDistanceTo(vec3d);
				}

				Vec3d vec3d1 = entity.getLook(1.0F);
				Vec3d vec3d2 = vec3d.add(vec3d1.x * distance, vec3d1.y * distance, vec3d1.z * distance);
				AxisAlignedBB axisalignedbb = entity.getBoundingBox().expand(vec3d1.scale(distance)).grow(1.0D, 1.0D, 1.0D);
				EntityRayTraceResult entityraytraceresult = ProjectileHelper.rayTraceEntities(entity, vec3d, vec3d2, axisalignedbb, (p_215312_0_) -> {
					return !p_215312_0_.isSpectator() && p_215312_0_.canBeCollidedWith();
				}, d1);
				if (entityraytraceresult != null)
				{
					Entity entity1 = entityraytraceresult.getEntity();
					Vec3d vec3d3 = entityraytraceresult.getHitVec();
					double d2 = vec3d.squareDistanceTo(vec3d3);
					if (flag && d2 > 9.0D)
					{
						objectMouseOver = BlockRayTraceResult.createMiss(vec3d3, Direction.getFacingFromVector(vec3d1.x, vec3d1.y, vec3d1.z), new BlockPos(vec3d3));
					} else if (d2 < d1 || objectMouseOver == null)
					{
						objectMouseOver = entityraytraceresult;
						if (entity1 instanceof LivingEntity || entity1 instanceof ItemFrameEntity)
						{
							mc.pointedEntity = entity1;
						}
					}
				}
				mc.getProfiler().endSection();
			}
		}
		
		ClientPlayerEntity player = mc.player;
		if (objectMouseOver == null)
		{
			 LogManager.getLogger().error("Null returned as 'hitResult', mc shouldn't happen!");
		} else if (!player.isRowingBoat())
		{
			if (objectMouseOver.getType() == Type.ENTITY)
			{
				Entity target = ((EntityRayTraceResult)objectMouseOver).getEntity();
				ReforgingPacketHandler.INSTANCE.sendToServer(new ReforgingPacketHandler.ExtendedAttackPacket(target.getEntityId()));
				if (!player.isSpectator())
				{
					player.attackTargetEntityWithCurrentItem(((EntityRayTraceResult)objectMouseOver).getEntity());
					player.resetCooldown();
				}
				player.swingArm(Hand.MAIN_HAND);
				return true;
			}/*
			else if (objectMouseOver.getType() == Type.MISS && player.getHeldItemMainhand().getItem() instanceof SwordItem)
			{
				Vec3d lookVec = player.getLook(1);
				double x = lookVec.x * distance + player.getPosX();
				double y = lookVec.y * distance + player.getPosY();
				double z = lookVec.z * distance + player.getPosZ();
				ReforgingPacketHandler.INSTANCE.sendToServer(new ReforgingPacketHandler.NoTargetSweepPacket(x, y, z));
				if (!player.isSpectator())
				{
					ModEventHandler.noTargetSweep(player, x, y, z);
				}
				player.swingArm(Hand.MAIN_HAND);
				return true;
			}*/
		}
		return false;
	}
}