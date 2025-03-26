package cool.muyucloud.beehave.mixin;

import cool.muyucloud.beehave.Beehave;
import cool.muyucloud.beehave.config.Config;
import cool.muyucloud.beehave.util.TranslatorManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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

@Mixin(BeehiveBlock.class)
public abstract class BeehiveBlockMixin extends BaseEntityBlock {
    @Unique
    private static final TranslatorManager TRANSLATOR = Beehave.TRANSLATOR;
    @Unique
    private static final Config CONFIG = Beehave.CONFIG;

    protected BeehiveBlockMixin(Properties settings) {
        super(settings);
    }

    @Inject(method = "use", at = @At("HEAD"))
    private void onUseWithItem(BlockState blockState, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult blockHitResult, CallbackInfoReturnable<InteractionResult> cir) {
        boolean enable = CONFIG.getAsBoolean("beehive");
        if (world.isClientSide || hand.equals(InteractionHand.OFF_HAND) || !enable) {
            return;
        }
        ItemStack itemStack = player.getItemInHand(hand);
        if (beehave$handValid(itemStack)) {
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
        ListTag bees = be.writeBees();
        MutableComponent text = TRANSLATOR.translate("message.chat.beehive.title",
            be.getOccupantCount(), pos.getX(), pos.getY(), pos.getZ());
        for (Tag element : bees) {
            text.append("\n");
            text.append(beehave$getBeeInfo((CompoundTag) element));
        }
        return text;
    }

    @Unique
    private static MutableComponent beehave$getBeeInfo(CompoundTag beeData) {
        MutableComponent text = beehave$readName(beeData).append(": ");
        String isBaby = beeData.getInt("Age") < 0 ? "baby" : "adult";
        int ticksInHive = beeData.getInt("TicksInHive");
        int minOccupationTicks = beeData.getInt("MinOccupationTicks");
        text.append(TRANSLATOR.translate("message.chat.beehive.row",
            isBaby, ticksInHive, minOccupationTicks));
        if (ticksInHive >= minOccupationTicks) {
            text.withStyle(ChatFormatting.GOLD);
        }
        return text;
    }

    @Unique
    private static MutableComponent beehave$readName(CompoundTag entityData) {
        MutableComponent name = Component.literal("").append(EntityType.BEE.getDescription());
        if (entityData.contains("CustomName")) {
            name = Component.literal(entityData.getString("CustomName"));
        }
        return name;
    }

    @Unique
    private static Component beehave$genTextEmpty(BlockPos pos) {
        return TRANSLATOR.translate("message.chat.beehive.empty", pos.getX(), pos.getY(), pos.getZ());
    }

    @Unique
    private static boolean beehave$handValid(ItemStack stack) {
        return !Beehave.VALID_ITEMS.contains(stack.getItem());
    }
}
