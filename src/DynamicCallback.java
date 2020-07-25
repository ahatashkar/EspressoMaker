import com.intellij.psi.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DynamicCallback extends Callback{

    private ActivityEntity activityEntity;

    public DynamicCallback(ActivityEntity activityEntity){
        this.activityEntity = activityEntity;
    }

    @Override
    public void getCallbacks(){
        File xmlFile = new File(activityEntity.getLayout().getCanonicalPath());
        List<String> viewIds = getViewIds(xmlFile);
        if(viewIds != null && !viewIds.isEmpty()){
            List<String> viewIdList = new ArrayList<>();
            for (String viewId : viewIds){

                activityEntity.getJavaClass().accept(new JavaRecursiveElementVisitor() {
                    @Override
                    public void visitAssignmentExpression(PsiAssignmentExpression expression) {
                        super.visitAssignmentExpression(expression);
                        String expressionText = expression.getRExpression().getText();
                        if(expressionText.contains("findViewById") && expressionText.contains(viewId)){
                            String str = expression.getRExpression().getText().replace("findViewById(R.id.", "");
                            str = str.replace(")", "");
                            if(viewId.equalsIgnoreCase(str)){
                                String idName = expression.getLExpression().getText();
                                viewIdList.add(idName);

                            }
                        }
                    }
                });
            }

            if(!viewIdList.isEmpty()){
                for(String viewId : viewIdList) {

                    activityEntity.getJavaClass().accept(new JavaRecursiveElementVisitor() {
                        @Override
                        public void visitMethodCallExpression(PsiMethodCallExpression expression) {
                            super.visitMethodCallExpression(expression);

                            if (expression.getMethodExpression().getReferenceName().equalsIgnoreCase("setOnClickListener")) {
                                if(viewId.equalsIgnoreCase(expression.getMethodExpression().getQualifierExpression().getText())){

                                    expression.accept(new JavaRecursiveElementVisitor() {
                                        @Override
                                        public void visitNewExpression(PsiNewExpression expression) {
                                            super.visitNewExpression(expression);

                                            ButtonNavigationInfo handler = getButtonNavigationInfo(expression, viewId);
                                            if(handler != null)
                                                activityEntity.buttonNavigationInfoList.add(handler);

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
