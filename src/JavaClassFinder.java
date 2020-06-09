import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiJavaFile;

import java.util.ArrayList;
import java.util.List;

public class JavaClassFinder extends JavaRecursiveElementVisitor {

    List<PsiClass> javaClasses;

    public JavaClassFinder() {
        this.javaClasses = new ArrayList<>();
    }

    public List<PsiClass> getJavaClasses(){
        return javaClasses;
    }

    @Override
    public void visitJavaFile(PsiJavaFile psiJavaFile) {
        super.visitFile(psiJavaFile);

        if (psiJavaFile.getName().endsWith(".java") && !psiJavaFile.getName().equals("R.java")) {

            PsiClass[] psiClasses = psiJavaFile.getClasses();
            for (PsiClass psiClass : psiClasses) {
                javaClasses.add(psiClass);
            }

        }
    }





}
