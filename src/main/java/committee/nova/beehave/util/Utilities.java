package committee.nova.beehave.util;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import org.jetbrains.annotations.Nullable;

public class Utilities {
    public static MutableComponent getBeeInfoFromEntity(Bee bee) {
        final MutableComponent text = Component.empty().append(bee.getName()).append(": ");
        final BlockPos hivePos = bee.getHivePos();
        text.append(isHiveAvailable(bee.level(), hivePos) ?
                Component.translatable("message.chat.bee.info", hivePos.getX(), hivePos.getY(), hivePos.getZ()) :
                Component.translatable("message.chat.bee.homeless"));
        return text;
    }

    public static MutableComponent getBeeInfoFromTag(CompoundTag tag) {
        final CompoundTag data = tag.getCompound("EntityData");
        final MutableComponent text = readEntityNameFromData(data).append(": ");
        final String baby = data.getInt("Age") < 0 ? "baby" : "adult";
        return text.append(Component.translatable("message.chat.beehive.row",
                data.getInt("Age") < 0 ? "baby" : "adult",
                tag.getInt("TicksInHive"), tag.getInt("MinOccupationTicks")));
    }

    private static MutableComponent readEntityNameFromData(CompoundTag data) {
        return data.contains("CustomName") ?
                Component.Serializer.fromJson(data.getString("CustomName")) :
                EntityType.BEE.getDescription().copy();
    }

    public static boolean isHiveAvailable(Level level, @Nullable BlockPos pos) {
        if (pos == null) return false;
        if (!(level.getBlockEntity(pos) instanceof BeehiveBlockEntity hive)) return false;
        return !hive.isFull();
    }
}
