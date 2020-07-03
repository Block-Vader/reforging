package com.blockvader.reforging;

import java.util.EnumSet;

import net.minecraft.entity.ai.goal.Goal;

public class DoNothingGoal extends Goal{

	public DoNothingGoal() {
		this.setMutexFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.LOOK, Goal.Flag.MOVE, Goal.Flag.TARGET));
	}
	
	@Override
	public boolean shouldExecute()
	{
		return true;
	}

}
