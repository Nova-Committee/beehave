package cool.muyucloud.beehave.mixin;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import cool.muyucloud.beehave.Beehave;
import cool.muyucloud.beehave.access.BeehiveBlockEntityAccess;
import cool.muyucloud.beehave.config.Config;
import cool.muyucloud.beehave.util.TranslatorManager;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Objects;

@Mixin(BeehiveBlock.class)
public abstract class BeehiveBlockMixin extends BlockWithEntity {
    @Shadow @Final public static MapCodec<BeehiveBlock> CODEC;
    @Unique
    private static final TranslatorManager TRANSLATOR = Beehave.TRANSLATOR;
    @Unique
    private static final Config CONFIG = Beehave.CONFIG;

    protected BeehiveBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "onUseWithItem", at = @At("HEAD"))
    private void onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        boolean enable = CONFIG.getAsBoolean("beehive");
        if (world.isClient || hand.equals(Hand.OFF_HAND) || !enable) {
            return;
        }
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemInvalid(itemStack)) {
            return;
        }
        BeehiveBlockEntity be = (BeehiveBlockEntity) world.getBlockEntity(pos);
        if (be == null || be.hasNoBees()) {
            player.sendMessage(genTextEmpty(pos), false);
            return;
        }
        player.sendMessage(getBeesInfo(pos, be), false);
    }

    @NotNull
    private static MutableText getBeesInfo(BlockPos pos, BeehiveBlockEntity be) {
        List<BeehiveBlockEntity.BeeData> bees = ((BeehiveBlockEntityAccess) be).invokeCreateBeeData();
        MutableText text = TRANSLATOR.translate("message.chat.beehive.title",
                be.getBeeCount(), pos.getX(), pos.getY(), pos.getZ());
        for (BeehiveBlockEntity.BeeData element : bees) {
            text.append("\n");
            text.append(getBeeInfo(element));
        }
        return text;
    }

    private static MutableText getBeeInfo(BeehiveBlockEntity.BeeData beeData) {
        NbtComponent entityData = beeData.entityData();
        MutableText text = readName(entityData).append(": ");
        String isBaby = entityData.get(Codec.INT.fieldOf("Age")).getOrThrow() < 0 ?
            "baby" : "adult";
        int ticksInHive = beeData.ticksInHive();
        int minOccupationTicks = beeData.minTicksInHive();
        text.append(TRANSLATOR.translate("message.chat.beehive.row",
            isBaby, ticksInHive, minOccupationTicks));
        if (ticksInHive >= minOccupationTicks) {
            text.formatted(Formatting.GOLD);
        }
        return text;
    }

    private static MutableText readName(NbtComponent entityData) {
        MutableText name = Text.literal("").append(EntityType.BEE.getName());
        if (entityData.contains("CustomName")) {
            name = Text.literal(Objects.requireNonNull(entityData.get(Codec.STRING.fieldOf("CustomName")).getOrThrow()));
        }
        return name;
    }

    private static Text genTextEmpty(BlockPos pos) {
        return TRANSLATOR.translate("message.chat.beehive.empty", pos.getX(), pos.getY(), pos.getZ());
    }

    private static boolean itemInvalid(ItemStack stack) {
        return !Beehave.VALID_ITEMS.contains(stack.getItem());
    }
}
