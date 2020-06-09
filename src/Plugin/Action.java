package Plugin;

import Analysis.EspressoMaker;
import Utils.Utils;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiElement;
import com.intellij.ui.content.Content;
import org.jetbrains.annotations.NotNull;

public class Action extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {

        Project project = anActionEvent.getProject();
        PsiElement psiElement = anActionEvent.getData(LangDataKeys.PSI_ELEMENT);
        projectProcess(project, psiElement);
    }

    void projectProcess(Project project, PsiElement psiElement) {
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("EspressoMaker");
        ConsoleView consoleView = Utils.getConsoleView();
        if (consoleView == null) {
            consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole();
            Utils.setConsoleView(consoleView);
            Content content = toolWindow.getContentManager().getFactory().createContent(consoleView.getComponent(), "EspressoMaker", true);
            toolWindow.getContentManager().addContent(content);
        }
        toolWindow.show(null);
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            public void run() {
                ApplicationManager.getApplication().runReadAction(new EspressoMaker(project, psiElement));
            }
        });

    }
}
