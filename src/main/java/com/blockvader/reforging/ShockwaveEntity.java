package com.blockvader.reforging;

import com.blockvader.reforging.init.ModEffects;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ShockwaveEntity extends Entity{
	
	protected static final AxisAlignedBB TARGET_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
	private float damage;
	private int reach;
	private LivingEntity owner;
	private static final DataParameter<Float> DISTANCE_TRAVELED = EntityDataManager.createKey(Entity.class, DataSerializers.FLOAT);

	public ShockwaveEntity(EntityType<?> entityTypeIn, World worldIn) {
		super(entityTypeIn, worldIn);
		this.noClip = true;
	}

	@Override
	protected void registerData() {
		this.dataManager.register(DISTANCE_TRAVELED, 0.0F);
	}
	
	public void setOwner(LivingEntity owner)
	{
		this.owner = owner;
	}
	
	public void setDamage(float damage)
	{
		this.damage = damage;
	}
	
	public void setReach(int reach)
	{
		this.reach = reach;
	}
	
	public void setDistanceTraveled(float distance)
	{
		this.dataManager.set(DISTANCE_TRAVELED, distance);
	}
	
	@Override
	public void tick()
	{
		if (!this.world.isRemote)
		{
			boolean flag = false;
			int r = Math.round(this.dataManager.get(DISTANCE_TRAVELED));
			for (int x = -reach; x <= reach; x++)
			{
				for (int z = -reach; z <= reach; z++)
				{
					if (r == Math.round(Math.sqrt(x*x + z*z)))
					{
						for (int y = -1; y <= 1; y++)
						{
							BlockPos pos = new BlockPos(x + (int)this.getPosX(), y + (int)this.getPosY(), z + (int)this.getPosZ());
							BlockState state = this.world.getBlockState(pos);
							if (state.isSolid())
							{
								flag = true;
								if (this.world instanceof ServerWorld)
								{
									((ServerWorld)this.world).spawnParticle(new BlockParticleData(ParticleTypes.BLOCK, state), x + (int)this.getPosX(), y + (int)this.getPosY() + 1, z + (int)this.getPosZ(), 1, 0, 0, 0, 0);
								}
								for (LivingEntity living: this.world.getEntitiesWithinAABB(LivingEntity.class, TARGET_AABB.offset(pos.up())))
								{
									if (living.onGround)
									{
										living.attackEntityFrom(new IndirectEntityDamageSource("shockwave", this, this.owner), this.damage);
										living.knockBack(this, 0.4F, 0, 0);
										living.addPotionEffect(new EffectInstance(ModEffects.STUN, 30));
									}
								}
							}
						}
					}
				}
			}
			if (!flag) this.remove();
			else
			{
				this.dataManager.set(DISTANCE_TRAVELED, this.dataManager.get(DISTANCE_TRAVELED) + 0.3F);
				if (this.dataManager.get(DISTANCE_TRAVELED) > this.reach) this.remove();
			}
		}
		super.tick();
	}

	@Override
	protected void readAdditional(CompoundNBT compound)
	{
		this.reach = compound.getInt("Reach");
		this.damage = compound.getFloat("Damage");
		this.dataManager.set(DISTANCE_TRAVELED, compound.getFloat("DistanceTraveled"));
	}

	@Override
	protected void writeAdditional(CompoundNBT compound)
	{
		compound.putInt("Reach", this.reach);
		compound.putFloat("Damage", this.damage);
		compound.putFloat("DistanceTraveled", dataManager.get(DISTANCE_TRAVELED));
	}

	@Override
	public IPacket<?> createSpawnPacket()
	{
		return new SSpawnObjectPacket(this);
	}

}
