package cool.muyucloud.beehave.mixin;

import cool.muyucloud.beehave.Beehave;
import cool.muyucloud.beehave.config.Config;
import cool.muyucloud.beehave.util.TranslatorManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnimalEntity.class)
public abstract class AnimalEntityMixin extends PassiveEntity {
    private static final TranslatorManager TRANSLATOR = Beehave.TRANSLATOR;
    private static final Config CONFIG = Beehave.CONFIG;

    protected AnimalEntityMixin(EntityType<? extends PassiveEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "interactMob", at = @At("RETURN"))
    public void interactMob(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        boolean enable = CONFIG.getAsBoolean("bee");
        if (!enable) {
            return;
        }
        boolean holdBreedingItem = ((AnimalEntity) (Object) this).isBreedingItem(player.getStackInHand(hand));
        if (this.world.isClient || hand.equals(Hand.OFF_HAND) || holdBreedingItem) {
            return;
        }
        if (!(((AnimalEntity) (Object) this) instanceof BeeEntity)) {
            return;
        }
        BeeEntity entity = ((BeeEntity) (Object) this);
        MutableText beeInfo = getBeeInfo(entity);
        player.sendMessage(beeInfo, false);
    }

    private static MutableText getBeeInfo(BeeEntity entity) {
        MutableText text = new LiteralText("").append(entity.getName()).append(": ");
        if (entity.getHivePos() != null && hiveAvailable(entity)) {
            BlockPos pos = entity.getHivePos();
            text.append(TRANSLATOR.translate(
                "message.chat.bee.info", pos.getX(), pos.getY(), pos.getZ()));
        } else {
            text.append(TRANSLATOR.translate("message.chat.bee.homeless"));
        }
        return text;
    }

    private static boolean hiveAvailable(BeeEntity entity) {
        return entity.hasHive() &&
            ((BeeEntityAccessor) entity).invokeDoesHiveHaveSpace(entity.getHivePos());
    }
}
