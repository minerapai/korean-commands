package kr.kocommands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Korean aliases for commands of other mods, e.g. /업적 for /achv: "alias": "target command" in
 * config/korean-commands.json (the target may be a sub command, "tower shop"). An alias does what its target does,
 * with the same arguments and permission. They are added after every other mod registered its commands (a later
 * event phase), again on every /reload. Targets that do not exist are skipped.
 */
public class KoreanCommands implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("kocommands");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Identifier LATE = Identifier.of("kocommands", "late");

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.addPhaseOrdering(Event.DEFAULT_PHASE, LATE);
        CommandRegistrationCallback.EVENT.register(LATE, (dispatcher, registryAccess, environment) -> register(dispatcher));
    }

    private static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        int added = 0;

        for (Map.Entry<String, String> entry : loadConfig().entrySet()) {
            String alias = entry.getKey().trim();
            List<String> path = Arrays.stream(entry.getValue().trim().split(" +")).filter(s -> !s.isEmpty()).toList();
            CommandNode<ServerCommandSource> target = path.isEmpty() ? null : dispatcher.findNode(path);

            if (alias.isEmpty() || alias.contains(" ") || target == null) {
                LOGGER.warn("skipping /{}: no command /{}", alias, entry.getValue());
                continue;
            }

            if (dispatcher.getRoot().getChild(alias) != null) {
                LOGGER.warn("skipping /{}: that command already exists", alias);
                continue;
            }

            LiteralArgumentBuilder<ServerCommandSource> builder = CommandManager.literal(alias).requires(target.getRequirement());

            if (target.getCommand() != null) {
                builder.executes(target.getCommand());
            }

            if (!target.getChildren().isEmpty()) {
                builder.redirect(target); // its sub commands and arguments
            }

            dispatcher.register(builder);
            added++;
        }

        LOGGER.info("added {} Korean command aliases", added);
    }

    private static Map<String, String> defaults() {
        Map<String, String> aliases = new LinkedHashMap<>();
        aliases.put("업적", "achv");
        aliases.put("퀘스트", "quest");
        aliases.put("라이벌", "quest rival");
        aliases.put("타워", "tower");
        aliases.put("타워상점", "tower shop");
        aliases.put("타워순위", "tower top");
        aliases.put("집", "home");
        aliases.put("집설정", "sethome");
        aliases.put("집삭제", "delhome");
        aliases.put("집목록", "homes");
        aliases.put("알기술", "tmegg");
        aliases.put("이벤트", "pokeevent");
        aliases.put("포켓몬아이템", "pokeitem");
        return aliases;
    }

    private static Map<String, String> loadConfig() {
        Path file = FabricLoader.getInstance().getConfigDir().resolve("korean-commands.json");

        try {
            if (Files.notExists(file)) {
                try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                    GSON.toJson(defaults(), writer);
                }
            }

            try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                Map<String, String> read = GSON.fromJson(reader, new TypeToken<LinkedHashMap<String, String>>() { }.getType());
                return read != null ? read : Map.of();
            }
        } catch (Exception e) {
            LOGGER.error("Could not read {}, using the defaults", file, e);
            return defaults();
        }
    }
}
