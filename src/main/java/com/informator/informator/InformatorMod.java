package com.informator.informator;

import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.slf4j.Logger;

@Mod(InformatorMod.MOD_ID)
public class InformatorMod {
    public static final String MOD_ID = "informator";
    public static final Logger LOGGER = LogUtils.getLogger();

    public InformatorMod() {
        NeoForge.EVENT_BUS.register(this);
        LOGGER.info("Informator мод загружен");
    }

    @SubscribeEvent
    public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (event.getTo() == Level.NETHER) {
            LOGGER.info("Игрок {} вошел в незер, ищем ближайшую крепость", player.getName().getString());

            runLocateFortressCommand(player);
        }
    }

    private void runLocateFortressCommand(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        try {
            serverLevel.getServer()
                    .getCommands()
                    .performPrefixedCommand(
                            player.createCommandSourceStack(),
                            "locate structure minecraft:fortress"
                    );
        } catch (Exception e) {
            LOGGER.error("Ошибка при выполнении locate для игрока {}: {}", player.getName().getString(), e.getMessage(), e);
            Component errorMessage = Component.literal("§6[Informator] §cОшибка при выполнении команды locate.");
            player.sendSystemMessage(errorMessage);
        }
    }
}

