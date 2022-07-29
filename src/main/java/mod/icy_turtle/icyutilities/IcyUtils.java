package mod.icy_turtle.icyutilities;

import net.minecraft.entity.Entity;

public class IcyUtils
{
	private IcyUtils(){}

	public static float roundToClosestLockAngle(float input)
	{
		return Math.round(input/IcyUtilitiesClient.ANGLE_TO_LOCK_AT)*IcyUtilitiesClient.ANGLE_TO_LOCK_AT;
	}

	public static void lockToClosestLockAngle(Entity entity)
	{
		entity.setYaw(roundToClosestLockAngle(entity.getYaw()));
	}
}