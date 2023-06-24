package committee.nova.beehave.event.handler;

import committee.nova.beehave.Beehave;
import committee.nova.beehave.util.Utilities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ForgeEventHandler {
    @SubscribeEvent
    public static void onInteractBee(PlayerInteractEvent.EntityInteract e) {
        if (!Beehave.bee.get() || !(e.getTarget() instanceof Bee bee)) return;
        final Level level = bee.level();
        if (level.isClientSide || e.getHand().equals(InteractionHand.OFF_HAND)) return;
        final Player player = e.getEntity();
        if (player.isHolding(bee::isFood)) return;
        player.displayClientMessage(Utilities.getBeeInfoFromEntity(bee), false);
    }

    @SubscribeEvent
    public static void onInteractBeehive(PlayerInteractEvent.RightClickBlock e) {
        if (!Beehave.beehive.get()) return;
        if (e.getHand().equals(InteractionHand.OFF_HAND)) return;
        final Level level = e.getLevel();
        if (level.isClientSide) return;
        final Player player = e.getEntity();
        if (player.isHolding(s -> !s.isEmpty())) return;
        final BlockPos pos = e.getPos();
        if (!(level.getBlockEntity(pos) instanceof BeehiveBlockEntity hive) || hive.isEmpty()) {
            player.displayClientMessage(Component.translatable("message.chat.beehive.empty",
                    pos.getX(), pos.getY(), pos.getZ()), false);
            return;
        }
        player.displayClientMessage(Component.translatable("message.chat.beehive.title", hive.getOccupantCount(),
                pos.getX(), pos.getY(), pos.getZ()), false);
        hive.writeBees().forEach(t -> {
            if ((t instanceof CompoundTag tag)) player.displayClientMessage(Utilities.getBeeInfoFromTag(tag), false);
        });
    }

    //@SubscribeEvent
    //public static void onRegisterCmd(RegisterCommandsEvent e) {
    //    e.getDispatcher().register(Commands.literal("beehave")
    //            .executes()
    //            .requires(p -> p.hasPermission(p.getServer().getOperatorUserPermissionLevel())));
    //}
}
