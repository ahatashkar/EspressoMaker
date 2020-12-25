import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;

public class FragmentFinder extends JavaRecursiveElementVisitor {

    boolean isFragment = false;
    PsiClass psiClass;
    String layoutName;

    public FragmentFinder(PsiClass psiClass, String layoutName) {
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
                            isFragment = true;
                    }

                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }


    public boolean isFragment(){
        return isFragment;
    }
}
