package mc.minecraft.notch.console;

import java.util.List;

public interface CommandProcessor {

    Result execute(String command, List<String> arguments);

    final class Result {
        public final String result;
        public final boolean isSuccess;

        Result(Throwable throwable) {
            this(throwable.getMessage(), false);
        }

        Result() {
            this("OK", true);
        }

        Result(String result, boolean isSuccess) {
            this.result = result;
            this.isSuccess = isSuccess;
        }
    }
}
