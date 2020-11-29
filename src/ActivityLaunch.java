import com.intellij.openapi.vfs.VirtualFile;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ActivityLaunch implements TestStrategy {

    @Override
    public String testGenerator(ActivityEntity entity) {

        String ACTIVITY_SCENARIO_LAUNCH = "ActivityScenario.launch([className].class);\n";
        String ON_VIEW_CHECK = "onView(withId(R.id.[id])).check(matches(isDisplayed()));\n";

        List<String> idList = getRootLayoutId(entity.getLayout());

        StringBuilder testCode = new StringBuilder();
        testCode.append(JavaCodeStrings.METHOD_HEADER.replace("[methodName]", "isActivityInView"));
        testCode.append(ACTIVITY_SCENARIO_LAUNCH.replace("[className]", entity.getJavaClass().getName()));

        if(idList != null){
            for(String id : idList)
                testCode.append(ON_VIEW_CHECK.replace("[id]", id));
        }

        testCode.append(JavaCodeStrings.R_BRACKET);

        return testCode.toString();
    }

    List<String> getRootLayoutId(VirtualFile layout) {
        final List<String> viewIds = new ArrayList<>();

        if(layout.getCanonicalPath() != null) {
            File xmlFile = new File(layout.getCanonicalPath());

            try {

                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser saxParser = factory.newSAXParser();
                saxParser.parse(xmlFile, new DefaultHandler() {
                    @Override
                    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                        super.startElement(uri, localName, qName, attributes);

                        for(int i = 0; i < attributes.getLength(); i++){
                            String attributeQualifiedName = attributes.getQName(i);

                            if (attributeQualifiedName != null && attributeQualifiedName.toLowerCase().equalsIgnoreCase("android:id")) {
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


            } catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        return viewIds;




    }
}
