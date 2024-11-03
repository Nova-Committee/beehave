package cool.muyucloud.beehave.mixin;

import cool.muyucloud.beehave.access.BeehiveBlockEntityAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;

@Mixin(BeehiveBlockEntity.class)
public abstract class BeehiveBlockEntityMixin implements BeehiveBlockEntityAccess {
    @Shadow protected abstract List<BeehiveBlockEntity.Occupant> getBees();

    @Override
    public List<BeehiveBlockEntity.Occupant> beehave$invokeCreateBeeData() {
        return this.getBees();
    }
}
