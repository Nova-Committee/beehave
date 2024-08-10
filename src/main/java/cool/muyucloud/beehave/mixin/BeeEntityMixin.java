package cool.muyucloud.beehave.mixin;

import cool.muyucloud.beehave.access.BeeEntityAccess;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BeeEntity.class)
public abstract class BeeEntityMixin implements BeeEntityAccess {
    @Shadow protected abstract boolean doesHiveHaveSpace(BlockPos pos);

    @Override
    public boolean invokeDoesHiveHaveSpace(BlockPos pos) {
        return this.doesHiveHaveSpace(pos);
    }
}
