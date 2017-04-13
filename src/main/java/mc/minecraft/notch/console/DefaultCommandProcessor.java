package mc.minecraft.notch.console;

import mc.minecraft.notch.Game;
import mc.minecraft.notch.property.PropertyContainer;
import mc.minecraft.notch.property.PropertyReadonly;

import java.util.List;

public final class DefaultCommandProcessor implements CommandProcessor {
    private final Game game;
    private final PropertyContainer container;

    public DefaultCommandProcessor(Game game, PropertyContainer container) {
        this.game = game;
        this.container = container;
    }

    @Override
    public Result execute(String command, List<String> arguments) {
        if (command == null)
            return new Result("Empty command", false);
        if ("property".equalsIgnoreCase(command)) {
            if (arguments.size() == 1) {
                String key = arguments.get(0);
                PropertyReadonly property = container.property(key);
                return new Result(property.asString(), true);
            } else if (arguments.size() == 2) {
                String key = arguments.get(0);
                PropertyReadonly property = container.property(key);
                try {
                    container.update(property, arguments.get(1));
                    return new Result();
                } catch (Exception ex) {
                    return new Result(ex);
                }
            }
            return new Result();
        } else if ("quit".equalsIgnoreCase(command)) {
            game.stop();
            return new Result();
        }
        return new Result("Unknow command", false);
    }
}
