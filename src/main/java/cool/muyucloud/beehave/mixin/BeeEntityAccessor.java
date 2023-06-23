package cool.muyucloud.beehave.mixin;

import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BeeEntity.class)
public interface BeeEntityAccessor {
    @Invoker("doesHiveHaveSpace")
    boolean invokeDoesHiveHaveSpace(BlockPos pos);
}
