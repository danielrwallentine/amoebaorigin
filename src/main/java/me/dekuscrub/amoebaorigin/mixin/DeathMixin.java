package me.dekuscrub.amoebaorigin.mixin;

import me.dekuscrub.amoebaorigin.Amoebaorigin;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(ServerPlayerEntity.class)
public class DeathMixin {

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void attack(Entity target, CallbackInfo ci) {
        Amoebaorigin.resetOrigin(Objects.requireNonNull((ServerPlayerEntity)(Object)this));
    }
}