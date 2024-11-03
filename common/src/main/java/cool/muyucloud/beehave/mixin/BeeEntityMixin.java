package cool.muyucloud.beehave.mixin;

import cool.muyucloud.beehave.access.BeeEntityAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.Bee;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Bee.class)
public abstract class BeeEntityMixin implements BeeEntityAccess {
    @Shadow protected abstract boolean doesHiveHaveSpace(BlockPos pos);

    @Override
    public boolean beehave$invokeDoesHiveHaveSpace(BlockPos pos) {
        return this.doesHiveHaveSpace(pos);
    }
}
