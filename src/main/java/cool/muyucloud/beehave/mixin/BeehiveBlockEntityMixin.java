package cool.muyucloud.beehave.mixin;

import cool.muyucloud.beehave.access.BeehiveBlockEntityAccess;
import net.minecraft.block.entity.BeehiveBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(BeehiveBlockEntity.class)
public abstract class BeehiveBlockEntityMixin implements BeehiveBlockEntityAccess {
    @Shadow protected abstract List<BeehiveBlockEntity.BeeData> createBeesData();

    @Override
    public List<BeehiveBlockEntity.BeeData> invokeCreateBeeData() {
        return createBeesData();
    }
}
