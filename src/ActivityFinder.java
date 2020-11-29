import com.intellij.psi.*;

import java.util.Objects;

public class ActivityFinder extends JavaRecursiveElementVisitor {

    boolean isActivity = false;
    PsiClass psiClass;
    String layoutName;

    public ActivityFinder(PsiClass psiClass, String layoutName) {
        this.psiClass = psiClass;
        this.layoutName = layoutName.replaceAll(".xml", "");
    }

    @Override
    public void visitMethod(PsiMethod method) {
        super.visitMethod(method);

        method.getBody().accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitMethodCallExpression(PsiMethodCallExpression expression) {
                super.visitMethodCallExpression(expression);

                try {
                    if(!expression.getArgumentList().isEmpty()) {
                        String name = expression.getArgumentList().getExpressions()[0].getText().replaceAll("R.layout.", "");
                        if (name.equalsIgnoreCase(layoutName))
                            isActivity = true;
                    }

                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }


    public boolean isActivity(){
        return isActivity;
    }
}
