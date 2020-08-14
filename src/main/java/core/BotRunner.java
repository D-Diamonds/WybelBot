package core;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import polls.PollModule;
import stats.StatModule;
import ttt.TicTacToeModule;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BotRunner extends ListenerAdapter {

    private static final String BOT_NAME = "Wybel";

    private static final Map<String, Module<?>> MODULES = new HashMap<>();

    static {
        MODULES.put("tictactoe", new TicTacToeModule());
        MODULES.put("stat", new StatModule());
        MODULES.put("poll", new PollModule());
        MODULES.put("default", new DefaultModule());
    }

    public static void main(String[] args) throws LoginException {
        // builds discord interaction
        JDABuilder builder = new JDABuilder(args[0]);
        builder.addEventListeners(new BotRunner());
        builder.build();
        System.out.println();
    }

    public static String getBotName() {
        return BOT_NAME;
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if (!event.getAuthor().isBot()) {
            List<String> messagePhrases = List.of(event.getMessage().getContentDisplay().toLowerCase().split(" "));

            if (messagePhrases.size() > 0) {
                MODULES.values().forEach(module -> module.onMessageReceived(event, messagePhrases));
            }

        }
    }

    @Override
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
        Member member = event.getMember();
        Guild guild = event.getGuild();

        // adds default role to Diamond Elysium Discord server
        List<Role> roles = guild.getRolesByName("Tyro", true);
        if (guild.getName().equals("Diamond Elysium") && roles.size() > 0) {
            System.out.println("Adding role Tyro to " + member.getEffectiveName());
            guild.addRoleToMember(member, roles.get(0)).complete();
        }

        MODULES.forEach((moduleName, module) -> {
            if (!moduleName.equals("default")) {
                module.onGuildMemberJoin(event);
            }
        });
    }

    public static Module<?> getModule(String moduleName) {
        return MODULES.get(moduleName.toLowerCase());
    }

    public static Collection<Module<?>> getModules() {
        return MODULES.values();
    }
}