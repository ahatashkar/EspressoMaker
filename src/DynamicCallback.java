import com.intellij.psi.*;
import com.intellij.psi.scope.processor.VariablesProcessor;
import com.intellij.psi.scope.util.PsiScopesUtil;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;

import java.io.File;
import java.util.*;

public class DynamicCallback extends Callback{

    private Entity entity;

    public DynamicCallback(Entity entity){
        this.entity = entity;
    }

    @Override
    public void getCallbacks(){
        File xmlFile = new File(entity.getLayout().getCanonicalPath());
        List<String> viewIds = getViewIds(xmlFile);
        if(viewIds != null && !viewIds.isEmpty()){
            List<String> viewIdList = new ArrayList<>();

            for (String viewId : viewIds){

                entity.getJavaClass().accept(new JavaRecursiveElementVisitor() {
                    @Override
                    public void visitAssignmentExpression(PsiAssignmentExpression expression) {
                        super.visitAssignmentExpression(expression);
                        String expressionText = expression.getRExpression().getText();
                        if (expressionText.contains("findViewById") && expressionText.contains(viewId)) {
                            String str = expression.getRExpression().getText().replace("findViewById(R.id.", "");
                            str = str.replace(")", "");
                            if (viewId.equalsIgnoreCase(str)) {
                                String idName = expression.getLExpression().getText();
                                viewIdList.add(idName);

                            }
                        }
                    }
                });

                entity.getJavaClass().accept(new JavaRecursiveElementVisitor() {
                    @Override
                    public void visitVariable(PsiVariable variable) {
                        super.visitVariable(variable);

                        try {
                            String expressionText = variable.getInitializer().getText();
                            if (expressionText.contains("findViewById") && expressionText.contains(viewId)){
                                variable.getInitializer().accept(new JavaRecursiveElementVisitor() {
                                    @Override
                                    public void visitMethodCallExpression(PsiMethodCallExpression expression) {
                                        super.visitMethodCallExpression(expression);
                                        List<PsiExpression> list = Arrays.asList(expression.getArgumentList().getExpressions());
                                        String str = list.get(0).getText().replace("R.id.","");
                                        if (viewId.equalsIgnoreCase(str)){
                                            viewIdList.add(variable.getName());
                                        }
                                    }
                                });
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                });
            }

            if(!viewIdList.isEmpty()){
                for(String viewId : viewIdList) {

                    entity.getJavaClass().accept(new JavaRecursiveElementVisitor() {
                        @Override
                        public void visitMethodCallExpression(PsiMethodCallExpression expression) {
                            super.visitMethodCallExpression(expression);

                            if (expression.getMethodExpression().getReferenceName().equalsIgnoreCase("setOnClickListener")) {
                                if(viewId.equalsIgnoreCase(expression.getMethodExpression().getQualifierExpression().getText())){

                                    expression.accept(new JavaRecursiveElementVisitor() {
                                        @Override
                                        public void visitMethod(PsiMethod method) {
                                            super.visitMethod(method);

                                            if(method.getName().equalsIgnoreCase("onClick")){

                                                //TODO: only "new" expression in "Intent intent = new Intent(...)" is considered.
                                                method.getBody().accept(new JavaRecursiveElementVisitor() {
                                                    @Override
                                                    public void visitNewExpression(PsiNewExpression expression) {
                                                        super.visitNewExpression(expression);

                                                        ButtonNavigationInfo handler = getButtonNavigationInfo(expression, viewId);
                                                        if(handler != null)
                                                            entity.buttonNavigationInfoList.add(handler);
                                                    }
                                                });

                                                if(method.getBody().getText().contains("finish();")){

                                                    ButtonNavigationInfo handler = new ButtonNavigationInfo();
                                                    handler.setName(viewId);
                                                    handler.setNavigatedActivity("_finish");
                                                    entity.buttonNavigationInfoList.add(handler);
                                                }

                                            }
                                        }
                                    });

                                }

                            }
                        }
                    });
                }
            }



        }




    }
}
