package cool.muyucloud.beehave.access;

import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.gen.Invoker;

public interface BeeEntityAccess {
    boolean invokeDoesHiveHaveSpace(BlockPos pos);
}
