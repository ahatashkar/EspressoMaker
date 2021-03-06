import com.intellij.psi.PsiNewExpression;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class Callback {

    public abstract void getCallbacks();

    protected List<String> getViewIds(File xmlFile) {
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

    protected ButtonNavigationInfo getButtonNavigationInfo(PsiNewExpression expression, String viewId){

        if(expression.getClassReference() != null) {
            if (expression.getClassReference().getQualifiedName().equalsIgnoreCase("android.content.Intent")) {
                String str = expression.getText().replace("new Intent(", "");
                str = str.replace(")", "");
                String[] arr = str.split(",");
                str = arr[1].replace(".class", "");

                ButtonNavigationInfo handler = new ButtonNavigationInfo();
                handler.setName(viewId);
                handler.setNavigatedActivity(str);

                return handler;
            }
        }

        return null;
    }

}
