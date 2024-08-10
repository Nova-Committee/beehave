package cool.muyucloud.beehave.access;

import net.minecraft.util.math.BlockPos;

public interface BeeEntityAccess {
    boolean invokeDoesHiveHaveSpace(BlockPos pos);
}
