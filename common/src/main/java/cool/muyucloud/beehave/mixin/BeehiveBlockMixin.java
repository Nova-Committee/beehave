package cool.muyucloud.beehave.mixin;

import cool.muyucloud.beehave.Beehave;
import cool.muyucloud.beehave.access.BeehiveBlockEntityAccess;
import cool.muyucloud.beehave.config.Config;
import cool.muyucloud.beehave.util.TranslatorManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@Mixin(BeehiveBlock.class)
public abstract class BeehiveBlockMixin extends BaseEntityBlock {
    @Unique
    private static final TranslatorManager beehave$TRANSLATOR = Beehave.TRANSLATOR;
    @Unique
    private static final Config beehave$CONFIG = Beehave.CONFIG;

    protected BeehiveBlockMixin(Properties settings) {
        super(settings);
    }

    @Inject(method = "useItemOn", at = @At("HEAD"))
    private void onUseWithItem(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        boolean enable = beehave$CONFIG.getAsBoolean("beehive");
        if (world.isClientSide() || hand.equals(InteractionHand.OFF_HAND) || !enable) {
            return;
        }
        ItemStack itemStack = player.getItemInHand(hand);
        if (beehave$handValid(itemStack)) {
            return;
        }
        BeehiveBlockEntity be = (BeehiveBlockEntity) world.getBlockEntity(pos);
        if (be == null || be.isEmpty()) {
            player.sendSystemMessage(beehave$genTextEmpty(pos));
            return;
        }
        player.sendSystemMessage(beehave$getBeesInfo(pos, be));
    }

    @Unique
    @NotNull
    private static MutableComponent beehave$getBeesInfo(BlockPos pos, BeehiveBlockEntity be) {
        List<BeehiveBlockEntity.Occupant> bees = ((BeehiveBlockEntityAccess) be).beehave$invokeCreateBeeData();
        MutableComponent text = beehave$TRANSLATOR.translate("message.chat.beehive.title",
            be.getOccupantCount(), pos.getX(), pos.getY(), pos.getZ());
        for (BeehiveBlockEntity.Occupant element : bees) {
            text.append("\n");
            text.append(beehave$getBeeInfo(element));
        }
        return text;
    }

    @Unique
    private static MutableComponent beehave$getBeeInfo(BeehiveBlockEntity.Occupant beeData) {
        TypedEntityData<EntityType<?>> entityData = beeData.entityData();
        CompoundTag nbt = entityData.copyTagWithoutId();
        MutableComponent text = beehave$readName(nbt).append(": ");
        Optional<Integer> mayAge = nbt.getInt("Age");
        String ageName = mayAge.map(age -> age < 0 ? "baby" : "adult").orElse("unknown");
        int ticksInHive = beeData.ticksInHive();
        int minOccupationTicks = beeData.minTicksInHive();
        text.append(beehave$TRANSLATOR.translate("message.chat.beehive.row",
            ageName, ticksInHive, minOccupationTicks));
        if (ticksInHive >= minOccupationTicks) {
            text.withStyle(ChatFormatting.GOLD);
        }
        return text;
    }

    @Unique
    private static MutableComponent beehave$readName(CompoundTag nbt) {
        Optional<String> mayName = nbt.getString("CustomName");
        return mayName.map(Component::literal).orElseGet(() -> Component.literal("").append(EntityType.BEE.getDescription()));
    }

    @Unique
    private static Component beehave$genTextEmpty(BlockPos pos) {
        return beehave$TRANSLATOR.translate("message.chat.beehive.empty", pos.getX(), pos.getY(), pos.getZ());
    }

    @Unique
    private static boolean beehave$handValid(ItemStack stack) {
        return !Beehave.VALID_ITEMS.contains(stack.getItem());
    }
}
