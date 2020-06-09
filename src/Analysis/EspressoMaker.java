package Analysis;

import Utils.Utils;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;

import java.util.List;

public class EspressoMaker implements Runnable {

    private Project project;
    private PsiElement psiElement;
    private List<PsiClass> projectJavaClasses;

    private VirtualFile sourceDirectory;

    public EspressoMaker(Project project, PsiElement psiElement){
        this.project = project;
        this.psiElement = psiElement;
    }

    @Override
    public void run() {

        Utils.showMessage("Start processing project: " + project.getName());

    }
}
