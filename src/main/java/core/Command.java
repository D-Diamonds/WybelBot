package core;

import java.util.Arrays;

public abstract class Command {

    private final String name;
    private final String description;

    private final String[] aliases;
    private final String[] args;
    private final String[] optinalArgs;

    private final Module<?> module;

    public Command(Module<?> module, String name, String description, String[] aliases, String[] args) {
        this(module, name, description, aliases, args, null);
    }

    public Command(Module<?> module, String name, String description, String[] aliases, String[] args, String[] optinalArgs) {
        this.module = module;
        this.name = name;
        this.description = description;
        this.aliases = aliases;
        this.args = args;
        this.optinalArgs = optinalArgs;
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

    public void execute() {

    }


}
