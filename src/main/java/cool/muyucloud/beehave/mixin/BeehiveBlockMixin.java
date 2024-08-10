package cool.muyucloud.beehave.mixin;

import cool.muyucloud.beehave.Beehave;
import cool.muyucloud.beehave.config.Config;
import cool.muyucloud.beehave.util.TranslatorManager;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(BeehiveBlock.class)
public abstract class BeehiveBlockMixin extends BlockWithEntity {
    private static final TranslatorManager TRANSLATOR = Beehave.TRANSLATOR;
    private static final Config CONFIG = Beehave.CONFIG;

    protected BeehiveBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "onUse", at = @At("HEAD"))
    private void onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
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
            player.sendMessage(genTextEmpty(pos));
            return;
        }
        player.sendMessage(getBeesInfo(pos, be));
    }

    @NotNull
    private static MutableText getBeesInfo(BlockPos pos, BeehiveBlockEntity be) {
        NbtList bees = be.getBees();
        MutableText text = TRANSLATOR.translate("message.chat.beehive.title",
                be.getBeeCount(), pos.getX(), pos.getY(), pos.getZ());
        for (NbtElement element : bees) {
            text.append("\n");
            NbtCompound compound = (NbtCompound) element;
            text.append(getBeeInfo(compound));
        }
        return text;
    }

    private static MutableText getBeeInfo(NbtCompound compound) {
        NbtCompound entityData = compound.getCompound("EntityData");
        MutableText text = readName(entityData).append(": ");
        String isBaby = Objects.requireNonNull(entityData).getInt("Age") < 0 ?
            "baby" : "adult";
        int ticksInHive = compound.getInt("TicksInHive");
        int minOccupationTicks = compound.getInt("MinOccupationTicks");
        text.append(TRANSLATOR.translate("message.chat.beehive.row",
            isBaby, ticksInHive, minOccupationTicks));
        if (ticksInHive >= minOccupationTicks) {
            text.setStyle(Style.EMPTY.withColor(Formatting.GOLD));
        }
        return text;
    }

    private static MutableText readName(NbtCompound entityData) {
        MutableText name = Text.literal("").append(EntityType.BEE.getName());
        if (entityData.contains("CustomName")) {
            name = Text.Serializer.fromJson(entityData.getString("CustomName"));
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
