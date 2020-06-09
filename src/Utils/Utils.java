package Utils;

import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;

public class Utils {

    static ConsoleView consoleView;

    public static ConsoleView getConsoleView() {
        return Utils.consoleView;
    }

    public static void setConsoleView(ConsoleView consoleView) {
        Utils.consoleView = consoleView;
    }

    public static void showMessage(String message) {
        if (consoleView != null) {
            consoleView.print(String.format("%s%n", message),
                    ConsoleViewContentType.NORMAL_OUTPUT);
        }
    }
}
