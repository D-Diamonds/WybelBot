package core.commands;

import core.Module;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public abstract class Command {

    private final String name;
    private final String description;

    private final String[] aliases;
    private final String[] args;
    private final String[] optinalArgs;

    protected final Module<?> module;

    private final int minArgs;

    public Command(Module<?> module, String name, String description, String[] aliases, String[] args) {
        this(module, name, description, aliases, args, null);
    }

    public Command(Module<?> module, String name, String description, @Nonnull String[] aliases, String[] args, String[] optionalArgs) {
        this.module = module;
        this.name = name;
        this.description = description;
        this.aliases = aliases;
        this.args = args;
        this.optinalArgs = optionalArgs;

        this.minArgs = (args != null ? args.length : 0);
    }

    public String getName() {
        return name;
    }

    public String[] getAliases() {
        return aliases;
    }

    public String getDescription() {
        return "**" + description + "**";
    }

    public String getHelpMessage() {
        StringBuilder stringBuilder = new StringBuilder();

        String alias = Arrays.stream(aliases).findFirst().orElse(null);
        if (alias != null) {
            stringBuilder.append(alias).append(" ");

            for (String arg : args) {
                stringBuilder.append("[").append(arg).append("]").append(" ");
            }

            for (String arg : optinalArgs) {
                stringBuilder.append("<").append(arg).append(">").append(" ");
            }
        }

        return stringBuilder.toString().stripTrailing();
    }

    public void execute(MessageReceivedEvent event, List<String> arguments) {

    }

    public boolean isValidInput(List<String> phrases) {
        return phrases.size() >= minArgs;
    }


}
