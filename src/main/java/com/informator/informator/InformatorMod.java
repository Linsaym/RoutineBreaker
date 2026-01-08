package com.informator.informator;

import com.mojang.logging.LogUtils;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.slf4j.Logger;

@Mod(InformatorMod.MOD_ID)
public class InformatorMod {
    public static final String MOD_ID = "informator";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final String TAG_TIME_TOTAL_MS = "informator_time_total_ms";
    private static final String TAG_TIME_JOIN_MS = "informator_time_join_ms";

    public InformatorMod() {
        NeoForge.EVENT_BUS.register(this);
        LOGGER.info("Informator мод загружен");
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        registerCommands(event.getDispatcher());
    }

    private void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("informator")
                        .then(
                                Commands.literal("timeplayed")
                                        .executes(ctx -> {
                                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                                            long totalMs = getTotalTimePlayedMs(player);
                                            player.sendSystemMessage(Component.literal(
                                                    "§6[Informator] §fРеального времени в мире: §e" + formatDurationMs(totalMs)
                                            ));
                                            return 1;
                                        })
                        )
                        .then(
                                Commands.literal("time")
                                        .executes(ctx -> {
                                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                                            long totalMs = getTotalTimePlayedMs(player);
                                            player.sendSystemMessage(Component.literal(
                                                    "§6[Informator] §fРеального времени в мире: §e" + formatDurationMs(totalMs)
                                            ));
                                            return 1;
                                        })
                        )
        );
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        CompoundTag data = player.getPersistentData();
        if (!data.contains(TAG_TIME_JOIN_MS)) {
            data.putLong(TAG_TIME_JOIN_MS, System.currentTimeMillis());
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        storeSessionTime(player);
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        if (!(event.getOriginal() instanceof ServerPlayer original) || !(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        CompoundTag originalData = original.getPersistentData();
        CompoundTag data = player.getPersistentData();

        if (originalData.contains(TAG_TIME_TOTAL_MS)) {
            data.putLong(TAG_TIME_TOTAL_MS, originalData.getLong(TAG_TIME_TOTAL_MS).orElse(0L));
        }
        if (originalData.contains(TAG_TIME_JOIN_MS)) {
            data.putLong(TAG_TIME_JOIN_MS, originalData.getLong(TAG_TIME_JOIN_MS).orElse(0L));
        }
    }

    @SubscribeEvent
    public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (event.getTo() == Level.NETHER) {
            LOGGER.info("Игрок {} вошел в незер, ищем ближайшую крепость", player.getName().getString());

            findAndSendNearestFortress(player);
        }
    }

    private void findAndSendNearestFortress(ServerPlayer player) {
        try {
            ServerLevel serverLevel = (ServerLevel) player.level();
            serverLevel.getServer()
                    .getCommands()
                    .performPrefixedCommand(
                            player.createCommandSourceStack().withPermission(4),
                            "locate structure minecraft:fortress"
                    );

        } catch (Exception e) {
            LOGGER.error("Ошибка поиска крепости", e);
            player.sendSystemMessage(
                Component.literal("§6[Informator] §cОшибка при поиске крепости.")
            );
        }
    }

    private static long getTotalTimePlayedMs(ServerPlayer player) {
        CompoundTag data = player.getPersistentData();
        long total = data.getLong(TAG_TIME_TOTAL_MS).orElse(0L);

        if (data.contains(TAG_TIME_JOIN_MS)) {
            long join = data.getLong(TAG_TIME_JOIN_MS).orElse(0L);
            long now = System.currentTimeMillis();
            if (now > join) {
                total += (now - join);
            }
        }

        return total;
    }

    private static void storeSessionTime(ServerPlayer player) {
        CompoundTag data = player.getPersistentData();
        if (!data.contains(TAG_TIME_JOIN_MS)) {
            return;
        }

        long join = data.getLong(TAG_TIME_JOIN_MS).orElse(0L);
        long now = System.currentTimeMillis();
        if (now <= join) {
            data.remove(TAG_TIME_JOIN_MS);
            return;
        }

        long session = now - join;
        long total = data.getLong(TAG_TIME_TOTAL_MS).orElse(0L);
        data.putLong(TAG_TIME_TOTAL_MS, total + session);
        data.remove(TAG_TIME_JOIN_MS);
    }

    private static String formatDurationMs(long ms) {
        long totalSeconds = ms / 1000;
        long seconds = totalSeconds % 60;
        long totalMinutes = totalSeconds / 60;
        long minutes = totalMinutes % 60;
        long hours = totalMinutes / 60;

        if (hours > 0) {
            return hours + "ч " + minutes + "м " + seconds + "с";
        }
        if (minutes > 0) {
            return minutes + "м " + seconds + "с";
        }
        return seconds + "с";
    }
}
