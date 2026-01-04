package com.toppacktoggle.toppacktoggle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Mod(TopPackToggleMod.MOD_ID)
public class TopPackToggleMod {
    public static final String MOD_ID = "toppacktoggle";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FMLPaths.CONFIGDIR.get().resolve("toppacktoggle.json");

    private static final KeyMapping.Category KEY_CATEGORY = new KeyMapping.Category(ResourceLocation.fromNamespaceAndPath(MOD_ID, "main"));
    private static KeyMapping TOGGLE_KEY;

    private static volatile String rememberedPackId;

    public TopPackToggleMod(IEventBus modBus) {
        modBus.addListener(TopPackToggleMod::onRegisterKeyMappings);
        NeoForge.EVENT_BUS.addListener(TopPackToggleMod::onClientTick);
        loadConfig();
        LOGGER.info("TopPackToggle мод загружен");
    }

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.registerCategory(KEY_CATEGORY);
        TOGGLE_KEY = new KeyMapping(
                "key." + MOD_ID + ".toggle",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_0,
                KEY_CATEGORY
        );
        event.register(TOGGLE_KEY);
    }

    public static void onClientTick(ClientTickEvent.Post event) {
        if (TOGGLE_KEY == null) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }

        while (TOGGLE_KEY.consumeClick()) {
            handleTogglePressed(mc);
        }
    }

    private static void syncSelectedPacksToRepository(Minecraft mc, List<String> selected) {
        try {
            PackRepository repo = mc.getResourcePackRepository();

            Method m;
            try {
                m = PackRepository.class.getMethod("setSelected", List.class);
                m.invoke(repo, selected);
                return;
            } catch (ReflectiveOperationException ignored) {
            }

            try {
                m = PackRepository.class.getMethod("setSelected", Collection.class);
                m.invoke(repo, selected);
                return;
            } catch (ReflectiveOperationException ignored) {
            }

            try {
                m = PackRepository.class.getMethod("setSelected", Iterable.class);
                m.invoke(repo, selected);
            } catch (ReflectiveOperationException ignored) {
            }

        } catch (Throwable ignored) {
        }
    }

    private static void handleTogglePressed(Minecraft mc) {
        if (rememberedPackId == null || rememberedPackId.isBlank()) {
            List<String> enabled = mc.options.resourcePacks;
            if (enabled == null || enabled.isEmpty()) {
                sendChat(mc, "§6[TopPackToggle] §cНет включённых ресурс-паков.");
                return;
            }

            String topPackId = enabled.get(enabled.size() - 1);
            rememberedPackId = topPackId;
            saveConfig();

            String displayName = resolvePackName(mc, topPackId);
            sendChat(mc, "§6[TopPackToggle] §aЗапомнен ресурс-пак: §f" + displayName);
            return;
        }

        toggleRememberedPack(mc);
    }

    private static void toggleRememberedPack(Minecraft mc) {
        List<String> current = mc.options.resourcePacks;
        if (current == null) {
            current = new ArrayList<>();
        } else {
            current = new ArrayList<>(current);
        }

        boolean enabled = current.contains(rememberedPackId);
        if (enabled) {
            current.removeIf(rememberedPackId::equals);
        } else {
            current.removeIf(rememberedPackId::equals);
            current.add(rememberedPackId);
        }

        syncSelectedPacksToRepository(mc, current);

        mc.options.resourcePacks = current;
        mc.options.save();
        mc.reloadResourcePacks();

        String displayName = resolvePackName(mc, rememberedPackId);
        if (enabled) {
            sendChat(mc, "§6[TopPackToggle] §eОтключён ресурс-пак: §f" + displayName);
        } else {
            sendChat(mc, "§6[TopPackToggle] §aВключён ресурс-пак: §f" + displayName);
        }
    }

    private static void sendChat(Minecraft mc, String message) {
        LocalPlayer player = mc.player;
        if (player == null) {
            return;
        }
        player.displayClientMessage(Component.literal(message), false);
    }

    private static String resolvePackName(Minecraft mc, String packId) {
        try {
            PackRepository repo = mc.getResourcePackRepository();
            Pack pack = repo.getPack(packId);
            if (pack != null) {
                return pack.getTitle().getString();
            }
        } catch (Throwable ignored) {
        }

        int slash = Math.max(packId.lastIndexOf('/'), packId.lastIndexOf('\\'));
        if (slash >= 0 && slash + 1 < packId.length()) {
            return packId.substring(slash + 1);
        }
        return packId;
    }

    private static void loadConfig() {
        if (!Files.exists(CONFIG_PATH)) {
            return;
        }

        try {
            String json = Files.readString(CONFIG_PATH, StandardCharsets.UTF_8);
            Config config = GSON.fromJson(json, Config.class);
            if (config != null && config.packId != null && !config.packId.isBlank()) {
                rememberedPackId = config.packId;
            }
        } catch (IOException | JsonSyntaxException e) {
            LOGGER.warn("Не удалось прочитать конфиг {}: {}", CONFIG_PATH, e.getMessage());
        }
    }

    private static void saveConfig() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            String json = GSON.toJson(new Config(rememberedPackId));
            Files.writeString(CONFIG_PATH, json, StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOGGER.warn("Не удалось сохранить конфиг {}: {}", CONFIG_PATH, e.getMessage());
        }
    }

    private record Config(String packId) {
    }
}
