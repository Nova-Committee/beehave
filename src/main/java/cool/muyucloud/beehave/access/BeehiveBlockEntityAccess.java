package cool.muyucloud.beehave.access;

import net.minecraft.block.entity.BeehiveBlockEntity;

import java.util.List;

public interface BeehiveBlockEntityAccess {
    List<BeehiveBlockEntity.BeeData> invokeCreateBeeData();
}
