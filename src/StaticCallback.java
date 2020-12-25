import com.intellij.psi.*;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StaticCallback extends Callback {

    private Entity entity;

    public StaticCallback(Entity entity){
        this.entity =  entity;
    }


    @Override
    public void getCallbacks(){
        File xmlFile = new File(entity.getLayout().getCanonicalPath());
        List<String> viewIds = getViewIds(xmlFile);
        if(viewIds != null && !viewIds.isEmpty()){
            for (String viewId : viewIds){
                String callbackMethodName = getCallbackMethodNameForView(xmlFile, viewId);
                if(callbackMethodName != null){

                    entity.getJavaClass().accept(new JavaRecursiveElementVisitor() {
                        @Override
                        public void visitMethod(PsiMethod method) {
                            super.visitMethod(method);

                            if(method.getName().equalsIgnoreCase(callbackMethodName)){
                                method.getBody().accept(new JavaRecursiveElementVisitor() {
                                    @Override
                                    public void visitNewExpression(PsiNewExpression expression) {
                                        super.visitNewExpression(expression);

                                        ButtonNavigationInfo handler = getButtonNavigationInfo(expression, viewId);
                                        if(handler != null)
                                            entity.buttonNavigationInfoList.add(handler);

                                    }
                                });
                            }

                        }
                    });

                }
            }
        }
    }



    private String getCallbackMethodNameForView(File xmlFile, String viewId) {
        if (xmlFile.exists() && xmlFile.isFile()) {
            try {
                final List<String> callbackMethodNames = new ArrayList<>();
                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser saxParser = factory.newSAXParser();
                saxParser.parse(xmlFile, new DefaultHandler() {
                    @Override
                    public void startElement(String uri, String localName,
                                             String qName, Attributes attributes) throws SAXException {
                        String tempViewId = null;
                        String tempCallbackMethodName = null;
                        for (int i = 0; i < attributes.getLength(); i++) {
                            String attributeQualifiedName = attributes.getQName(i);
                            if (attributeQualifiedName != null
                                    && attributeQualifiedName.toLowerCase()
                                    .equalsIgnoreCase("android:id")) {
                                String temp = attributes.getValue(i);
                                int index = temp.lastIndexOf('/');
                                if (index != -1) {
                                    temp = temp.substring(index + 1).trim();
                                }
                                tempViewId = temp;
                            } else if (attributeQualifiedName != null
                                    && attributeQualifiedName.toLowerCase()
                                    .equalsIgnoreCase("android:onclick")) {
                                tempCallbackMethodName = attributes.getValue(i);
                            }
                        }
                        if (tempViewId != null && tempViewId.equals(viewId)) {
                            callbackMethodNames.add(tempCallbackMethodName);
                        }
                    }
                });
                return callbackMethodNames.get(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;

    }
}
