package mod.icy_turtle.friendhighlighter.util;

import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;

public class LavaBlockedRaycastContext extends RaycastContext
{
	public LavaBlockedRaycastContext(Vec3d start, Vec3d end, ShapeType shapeType, Entity entity)
	{
		super(start, end, shapeType, FluidHandling.NONE, entity);
	}

	@Override
	public VoxelShape getFluidShape(FluidState state, BlockView world, BlockPos pos)
	{
		return state.isIn(FluidTags.LAVA) ? state.getShape(world, pos) : VoxelShapes.empty();
	}
}
