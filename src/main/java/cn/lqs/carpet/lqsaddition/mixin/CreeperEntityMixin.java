package cn.lqs.carpet.lqsaddition.mixin;

import cn.lqs.carpet.lqsaddition.LqsSettings;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.rule.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 高压苦力怕全头颅掉落。
 * <p>
 * 原版中闪电苦力怕炸死生物时通过 {@code onKilledOther} 走
 * {@code charged_creeper/root} 战利品表掉落对应头颅，但该表按精确类型匹配，
 * 只覆盖僵尸/骷髅/苦力怕/凋灵骷髅/猪灵五种怪物，不含玩家；且原版的
 * {@code headsDropped} 标志使整个爆炸最多只掉一个头颅。
 * 规则开启时：入口处重置 {@code headsDropped} 解除"每次爆炸只掉一个"的限制，
 * 使每个遇害者各掉各的头；并补齐玩家头颅（带皮肤数据）。
 * 原版不会掉落头颅的生物（猪灵蛮兵、牛、羊等）不掉落。
 * 是否生效由 {@code chargedCreeperDropsAllHeads} 规则控制。
 */
@Mixin(CreeperEntity.class)
public abstract class CreeperEntityMixin
{
    @Shadow
    private boolean headsDropped;

    /**
     * 解除原版"每次爆炸只掉一个头颅"的限制：规则开启时，每个遇害者处理前都重置标志，
     * 使同一次爆炸炸死的每个可掉头的实体都能掉落自己的头颅。
     */
    @Inject(
        method = "onKilledOther(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/damage/DamageSource;)Z",
        at = @At("HEAD")
    )
    private void lqsLiftHeadLimit(ServerWorld world, LivingEntity other, DamageSource source, CallbackInfoReturnable<Boolean> cir)
    {
        if (LqsSettings.chargedCreeperDropsAllHeads)
        {
            this.headsDropped = false;
        }
    }

    @Inject(
        method = "onKilledOther(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/damage/DamageSource;)Z",
        at = @At("TAIL")
    )
    private void lqsDropAllHeads(ServerWorld world, LivingEntity other, DamageSource source, CallbackInfoReturnable<Boolean> cir)
    {
        CreeperEntity self = (CreeperEntity) (Object) this;
        // 与原版 shouldDropLoot 门控一致（苦力怕无幼年形态，只需检查 DO_MOB_LOOT）
        if (!LqsSettings.chargedCreeperDropsAllHeads || !self.isCharged() || !world.getGameRules().getValue(GameRules.DO_MOB_LOOT) || this.headsDropped)
        {
            return;
        }
        if (!(other instanceof ServerPlayerEntity player))
        {
            // 五类原版战利品表已覆盖的怪物（其掉落时 headsDropped 已置位不会进入），
            // 以及原版不会掉落头颅的生物，均不额外掉落
            return;
        }
        ItemStack head = new ItemStack(Items.PLAYER_HEAD);
        head.set(DataComponentTypes.PROFILE, ProfileComponent.ofStatic(player.getGameProfile()));
        other.dropStack(world, head);
        this.headsDropped = true;
    }
}
