package com.shareddeath.shareddeath;

import com.mojang.logging.LogUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import org.slf4j.Logger;

@Mod(SharedDeathMod.MOD_ID)
public class SharedDeathMod {

    public static final String MOD_ID = "shareddeath";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static boolean processingChainDeath = false;

    public SharedDeathMod() {
        NeoForge.EVENT_BUS.register(this);
        LOGGER.info("SharedDeath загружен");
    }

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {

        if (!(event.getEntity() instanceof ServerPlayer deadPlayer)) {
            return;
        }

        if (processingChainDeath) {
            return;
        }

        MinecraftServer server = deadPlayer.level().getServer();

        processingChainDeath = true;

        try {
            // Более безопасный источник урона
            DamageSource source = deadPlayer.damageSources().generic();

            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                if (player != deadPlayer && !player.isDeadOrDying()) {
                    player.hurt(source, Float.MAX_VALUE);
                }
            }

            LOGGER.info(
                    "Игрок {} умер — все игроки на сервере были убиты",
                    deadPlayer.getName().getString()
            );

        } finally {
            processingChainDeath = false;
        }
    }
}
