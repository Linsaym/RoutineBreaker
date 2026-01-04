package com.witheraxehead.witheraxehead;

import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import org.slf4j.Logger;

@Mod(WitherAxeHeadMod.MOD_ID)
public class WitherAxeHeadMod {
    public static final String MOD_ID = "witheraxehead";
    public static final Logger LOGGER = LogUtils.getLogger();

    public WitherAxeHeadMod() {
        NeoForge.EVENT_BUS.register(this);
        LOGGER.info("WitherAxeHead мод загружен");
    }

    @SubscribeEvent
    public void onLivingDrops(LivingDropsEvent event) {
        if (!(event.getEntity() instanceof WitherSkeleton witherSkeleton)) {
            return;
        }

        if (witherSkeleton.level().isClientSide()) {
            return;
        }

        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) {
            return;
        }

        ItemStack weapon = player.getMainHandItem();
        if (!weapon.is(ItemTags.AXES)) {
            return;
        }

        ItemStack skull = new ItemStack(Items.WITHER_SKELETON_SKULL);

        event.getDrops().removeIf(drop -> drop.getItem().is(Items.WITHER_SKELETON_SKULL));
        event.getDrops().add(new ItemEntity(
                witherSkeleton.level(),
                witherSkeleton.getX(), witherSkeleton.getY(), witherSkeleton.getZ(),
                skull
        ));
    }
}
