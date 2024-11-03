package cool.muyucloud.beehave.mixin;

import cool.muyucloud.beehave.Beehave;
import cool.muyucloud.beehave.access.BeeEntityAccess;
import cool.muyucloud.beehave.config.Config;
import cool.muyucloud.beehave.util.TranslatorManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Animal.class)
public abstract class AnimalEntityMixin extends AgeableMob {
    @Unique
    private static final TranslatorManager beehave$TRANSLATOR = Beehave.TRANSLATOR;
    @Unique
    private static final Config beehave$CONFIG = Beehave.CONFIG;

    protected AnimalEntityMixin(EntityType<? extends AgeableMob> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "mobInteract", at = @At("RETURN"))
    public void interactMob(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        boolean enable = beehave$CONFIG.getAsBoolean("bee");
        if (!enable) {
            return;
        }
        boolean holdBreedingItem = ((Animal) (Object) this).isFood(player.getItemInHand(hand));
        if (this.level().isClientSide || hand.equals(InteractionHand.OFF_HAND) || holdBreedingItem) {
            return;
        }
        if (((Animal) (Object) this) instanceof Bee entity) {
            MutableComponent beeInfo = beehave$getBeeInfo(entity);
            player.displayClientMessage(beeInfo, false);
            beehave$playParticles(entity.getHivePos(), (ServerPlayer) player);
        }
    }

    @Unique
    private void beehave$playParticles(@Nullable BlockPos hivePos, @NotNull ServerPlayer target) {
        final int density = 3;
        Vec3 beePos = this.position();
        if (!beehave$hiveAvailable((Bee) (Object) this)) {
            return;
        }
        Vec3 delta = beePos.vectorTo(hivePos.getCenter());
        double distance = delta.length();
        Vec3 step = delta.multiply(1.0D / (density * distance), 1.0D / (density * distance), 1.0D / (density * distance));
        int count = (int) (distance * density);
        Vec3 pos = beePos;
        for (int i = 0; i <= count; ++i) {
            ((ServerLevel) this.level()).sendParticles(target, ParticleTypes.HAPPY_VILLAGER, false, pos.x(), pos.y(), pos.z(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
            pos = pos.add(step);
        }
    }

    @Unique
    private static MutableComponent beehave$getBeeInfo(Bee entity) {
        MutableComponent text = Component.literal("").append(entity.getName()).append(": ");
        if (entity.getHivePos() != null && beehave$hiveAvailable(entity)) {
            BlockPos pos = entity.getHivePos();
            text.append(beehave$TRANSLATOR.translate(
                "message.chat.bee.info", pos.getX(), pos.getY(), pos.getZ()));
        } else {
            text.append(beehave$TRANSLATOR.translate("message.chat.bee.homeless"));
        }
        return text;
    }

    @Unique
    private static boolean beehave$hiveAvailable(Bee entity) {
        return entity.hasHive() &&
            ((BeeEntityAccess) entity).beehave$invokeDoesHiveHaveSpace(entity.getHivePos());
    }
}
