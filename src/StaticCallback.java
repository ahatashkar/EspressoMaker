import com.intellij.psi.*;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StaticCallback {

    private ActivityEntity activityEntity;

    public StaticCallback(ActivityEntity activityEntity){
        this.activityEntity = activityEntity;
    }

    public void getCallbacks(){
        File xmlFile = new File(activityEntity.getLayout().getCanonicalPath());
        List<String> viewIds = getViewIds(xmlFile);
        if(viewIds != null && !viewIds.isEmpty()){
            for (String viewId : viewIds){
                String callbackMethodName = getCallbackMethodNameForView(xmlFile, viewId);
                if(callbackMethodName != null){

                    activityEntity.getJavaClass().accept(new JavaRecursiveElementVisitor() {
                        @Override
                        public void visitMethod(PsiMethod method) {
                            super.visitMethod(method);

                            if(method.getName().equalsIgnoreCase(callbackMethodName)){
                                method.getBody().accept(new JavaRecursiveElementVisitor() {
                                    @Override
                                    public void visitNewExpression(PsiNewExpression expression) {
                                        super.visitNewExpression(expression);

                                        if(expression.getClassReference().getQualifiedName().equalsIgnoreCase("android.content.Intent")){
                                            //TODO : is it possible to find a visitor to do this?
                                            String str = expression.getText().replace("new Intent(", "");
                                            str = str.replace(")","");
                                            String[] arr = str.split(",");
                                            str = arr[1].replace(".class","");

                                            ButtonHandler handler = new ButtonHandler();
                                            handler.setName(viewId);
                                            handler.setNavigatedActivity(str);
                                            activityEntity.buttonHandlers.add(handler);
                                        }
                                    }
                                });
                            }

                        }
                    });

                }
            }
        }
    }

    private List<String> getViewIds(File xmlFile) {
        if (xmlFile.exists() && xmlFile.isFile()) {
            try {
                final List<String> viewIds = new ArrayList<>();
                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser saxParser = factory.newSAXParser();
                saxParser.parse(xmlFile, new DefaultHandler() {
                    @Override
                    public void startElement(String uri, String localName,
                                             String qName, Attributes attributes) throws SAXException {
                        for (int i = 0; i < attributes.getLength(); i++) {
                            String attributeQualifiedName = attributes.getQName(i);
                            if (attributeQualifiedName != null
                                    && attributeQualifiedName.toLowerCase()
                                    .equalsIgnoreCase("android:id")) {
                                String viewId = attributes.getValue(i);
                                //it is needed to remove prefixes android:id="@+id/btn_modulo"
                                int index = viewId.lastIndexOf('/');
                                if (index != -1) {
                                    viewId = viewId.substring(index + 1).trim();
                                }
                                viewIds.add(viewId);
                            }
                        }
                    }
                });
                return viewIds;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
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
