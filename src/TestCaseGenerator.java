import com.intellij.openapi.vfs.VirtualFile;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TestCaseGenerator {

    private ActivityEntity entity;
    private File file;

    private String className;
    private List<String> idList;

    public TestCaseGenerator(File file, ActivityEntity entity){
        this.entity = entity;
        this.file = file;
        this.className = entity.getJavaClass().getName();
    }

    public void generate() {

        idList = getRootLayoutId(entity.getLayout());

        try {

            FileWriter writer = new FileWriter(file);
            writer.write("package EspressoMaker;\n\n");

            for(String str : Templates.getImports())
                writer.write(str + "\n");

            writer.write("\n@RunWith(AndroidJUnit4.class)\n");
            writer.write("public class " + className + "Test {\n\n");

            // test isActivityElementInView
            writer.write("@Test\n");
            writer.write("public void test_isActivityElementInView() {\n");
            writer.write("ActivityScenario.launch(" + className + ".class);\n");
            if(idList != null){
                for(String id : idList)
                    writer.write("onView(withId(R.id." + id + ")).check(matches(isDisplayed()));\n");
            }
            writer.write("}\n");






            writer.write("\n}");
            writer.close();

        } catch (Exception e){
            e.printStackTrace();
        }
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
