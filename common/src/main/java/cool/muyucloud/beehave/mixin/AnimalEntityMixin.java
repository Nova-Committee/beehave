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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
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

    @Unique
    public Animal beehave$adapt() {
        return (Animal) (Object) this;
    }

    @Inject(method = "mobInteract", at = @At("RETURN"))
    public void interactMob(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        boolean enable = beehave$CONFIG.getAsBoolean("bee");
        if (!enable) {
            return;
        }
        boolean holdBreedingItem = ((Animal) (Object) this).isFood(player.getItemInHand(hand));
        if (this.level().isClientSide() || hand.equals(InteractionHand.OFF_HAND) || holdBreedingItem) {
            return;
        }
        if (this.beehave$adapt() instanceof Bee entity) {
            MutableComponent beeInfo = beehave$getBeeInfo(entity);
            player.sendSystemMessage(beeInfo);
            beehave$playParticles();
        }
    }

    @Unique
    private void beehave$playParticles() {
        if (!(this.beehave$adapt() instanceof Bee bee) || bee.getHivePos() == null || !beehave$hiveAvailable()) {
            return;
        }
        final int density = 3;
        Vec3 beePos = this.position();
        BlockPos hivePos = bee.getHivePos();
        Vec3 delta = beePos.vectorTo(new Vec3(hivePos).add(0.5, 0.5, 0.5));
        double distance = delta.length();
        Vec3 step = delta.multiply(1.0D / (density * distance), 1.0D / (density * distance), 1.0D / (density * distance));
        int count = (int) (distance * density);
        Vec3 pos = beePos;
        for (int i = 0; i <= count; ++i) {
            ((ServerLevel) this.level()).sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.x(), pos.y(), pos.z(), 0, 0.0D, 0.0D, 0.0D, 0.0D);
            pos = pos.add(step);
        }
    }

    @Unique
    private static MutableComponent beehave$getBeeInfo(Bee entity) {
        MutableComponent text = Component.literal("").append(entity.getName()).append(": ");
        if (entity.getHivePos() != null && ((AnimalEntityMixin) (Object) entity).beehave$hiveAvailable()) {
            BlockPos pos = entity.getHivePos();
            text.append(beehave$TRANSLATOR.translate("message.chat.bee.info", pos.getX(), pos.getY(), pos.getZ()));
        } else {
            text.append(beehave$TRANSLATOR.translate("message.chat.bee.homeless"));
        }
        return text;
    }

    @Unique
    private boolean beehave$hiveAvailable() {
        if (!(this.beehave$adapt() instanceof Bee bee)) {
            return false;
        }
        return bee.hasHive() && ((BeeEntityAccess) bee).beehave$invokeDoesHiveHaveSpace(bee.getHivePos());
    }
}
