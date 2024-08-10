package cool.muyucloud.beehave.access;

import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.gen.Invoker;

public interface BeeEntityAccess {
    @Invoker("doesHiveHaveSpace")
    boolean invokeDoesHiveHaveSpace(BlockPos pos);
}
