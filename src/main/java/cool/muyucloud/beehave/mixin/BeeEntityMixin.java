package cool.muyucloud.beehave.mixin;

import cool.muyucloud.beehave.access.BeeEntityAccess;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BeeEntity.class)
public abstract class BeeEntityMixin extends AnimalEntity implements BeeEntityAccess {
    protected BeeEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    protected abstract boolean doesHiveHaveSpace(BlockPos pos);

    @Override
    @Invoker("doesHiveHaveSpace")
    public boolean invokeDoesHiveHaveSpace(BlockPos pos) {
        return this.doesHiveHaveSpace(pos);
    }
}
