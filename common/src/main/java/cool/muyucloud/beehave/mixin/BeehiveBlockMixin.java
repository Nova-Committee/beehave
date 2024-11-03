package cool.muyucloud.beehave.mixin;

import com.mojang.serialization.Codec;
import cool.muyucloud.beehave.Beehave;
import cool.muyucloud.beehave.access.BeehiveBlockEntityAccess;
import cool.muyucloud.beehave.config.Config;
import cool.muyucloud.beehave.util.TranslatorManager;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

@Mixin(BeehiveBlock.class)
public abstract class BeehiveBlockMixin extends BaseEntityBlock {
    @Unique
    private static final TranslatorManager TRANSLATOR = Beehave.TRANSLATOR;
    @Unique
    private static final Config CONFIG = Beehave.CONFIG;

    protected BeehiveBlockMixin(Properties settings) {
        super(settings);
    }

    @Inject(method = "useItemOn", at = @At("HEAD"))
    private void onUseWithItem(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        boolean enable = CONFIG.getAsBoolean("beehive");
        if (world.isClientSide || hand.equals(InteractionHand.OFF_HAND) || !enable) {
            return;
        }
        ItemStack itemStack = player.getItemInHand(hand);
        if (beehave$itemInvalid(itemStack)) {
            return;
        }
        BeehiveBlockEntity be = (BeehiveBlockEntity) world.getBlockEntity(pos);
        if (be == null || be.isEmpty()) {
            player.displayClientMessage(beehave$genTextEmpty(pos), false);
            return;
        }
        player.displayClientMessage(beehave$getBeesInfo(pos, be), false);
    }

    @Unique
    @NotNull
    private static MutableComponent beehave$getBeesInfo(BlockPos pos, BeehiveBlockEntity be) {
        List<BeehiveBlockEntity.Occupant> bees = ((BeehiveBlockEntityAccess) be).beehave$invokeCreateBeeData();
        MutableComponent text = TRANSLATOR.translate("message.chat.beehive.title",
                be.getOccupantCount(), pos.getX(), pos.getY(), pos.getZ());
        for (BeehiveBlockEntity.Occupant element : bees) {
            text.append("\n");
            text.append(beehave$getBeeInfo(element));
        }
        return text;
    }

    @Unique
    private static MutableComponent beehave$getBeeInfo(BeehiveBlockEntity.Occupant beeData) {
        CustomData entityData = beeData.entityData();
        MutableComponent text = beehave$readName(entityData).append(": ");
        String isBaby = entityData.read(Codec.INT.fieldOf("Age")).getOrThrow() < 0 ?
            "baby" : "adult";
        int ticksInHive = beeData.ticksInHive();
        int minOccupationTicks = beeData.minTicksInHive();
        text.append(TRANSLATOR.translate("message.chat.beehive.row",
            isBaby, ticksInHive, minOccupationTicks));
        if (ticksInHive >= minOccupationTicks) {
            text.withStyle(ChatFormatting.GOLD);
        }
        return text;
    }

    @Unique
    private static MutableComponent beehave$readName(CustomData entityData) {
        MutableComponent name = Component.literal("").append(EntityType.BEE.getDescription());
        if (entityData.contains("CustomName")) {
            name = Component.literal(Objects.requireNonNull(entityData.read(Codec.STRING.fieldOf("CustomName")).getOrThrow()));
        }
        return name;
    }

    @Unique
    private static Component beehave$genTextEmpty(BlockPos pos) {
        return TRANSLATOR.translate("message.chat.beehive.empty", pos.getX(), pos.getY(), pos.getZ());
    }

    @Unique
    private static boolean beehave$itemInvalid(ItemStack stack) {
        return !Beehave.VALID_ITEMS.contains(stack.getItem());
    }
}
