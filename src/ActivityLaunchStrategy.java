import com.intellij.openapi.vfs.VirtualFile;
import org.apache.xmlbeans.xml.stream.StartElement;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;
import org.xml.sax.helpers.DefaultHandler;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.io.*;
import java.util.*;

import java.io.*;
import java.util.*;
import org.jdom2.*;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ActivityLaunchStrategy implements TestStrategy {

    String ON_VIEW_CHECK = "onView(withId(R.id.[id])).check(matches(isDisplayed()));\n";
    String ON_VIEW_CHECK_VISIBILITY = "onView(withId(R.id.[id])).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.[visibility])));\n";

    @Override
    public String testGenerator(Entity entity) {

        List<Widget> widgets = new ArrayList<>();
        try{
            widgets = getLayoutViews(entity.getLayout());
        } catch (Exception e){
            e.printStackTrace();
        }

        StringBuilder testCode = new StringBuilder();
        testCode.append(JavaCodeStrings.METHOD_HEADER.replace("[methodName]", "isActivityInView"));
//        testCode.append(JavaCodeStrings.ACTIVITY_SCENARIO_LAUNCH.replace("[className]", entity.getJavaClass().getName()));

        for(Widget widget : widgets)
            testCode.append(ON_VIEW_CHECK_VISIBILITY.replace("[id]", widget.id).replace("[visibility]", widget.visibility.toUpperCase()));

        testCode.append(JavaCodeStrings.R_BRACKET);

        return testCode.toString();
    }

//    List<Widget> getRootLayoutId(VirtualFile layout){
////        final List<String> viewIds = new ArrayList<>();
//
//        List<Widget> widgetList = new ArrayList<>();
//        final String[] parent = {""};
//
//        if(layout.getCanonicalPath() != null) {
//            File xmlFile = new File(layout.getCanonicalPath());
//
//            try {
//
//                SAXParserFactory factory = SAXParserFactory.newInstance();
//                SAXParser saxParser = factory.newSAXParser();
//
//                Widget[] widget = {new Widget()};
//                Widget[] parentWidget = {new Widget()};
//
//                saxParser.parse(xmlFile, new DefaultHandler() {
//
//                    @Override
//                    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
//                        super.startElement(uri, localName, qName, attributes);
//
//                        if (parent[0].equalsIgnoreCase(""))
//                            parent[0] = qName;
//
//                        for(int i = 0; i < attributes.getLength(); i++){
//                            String attributeQualifiedName = attributes.getQName(i).toLowerCase();
//
//                            switch (attributeQualifiedName) {
//                                case "android:id":
//                                    String viewId = attributes.getValue(i);
//                                    //it is needed to remove prefixes android:id="@+id/btn_modulo"
//                                    int index = viewId.lastIndexOf('/');
//                                    if (index != -1) {
//                                        viewId = viewId.substring(index + 1).trim();
//                                    }
//
//                                    if (parent[0].equalsIgnoreCase(qName)) // Parent widget
//                                        parentWidget[0].id = viewId;
//                                    else
//                                        widget[0].id = viewId;
//
//                                    break;
//
//                                case "android:visibility":
//                                    if (parent[0].equalsIgnoreCase(qName)) // Parent widget
//                                        parentWidget[0].visibility = attributes.getValue(i).trim();
//                                    else
//                                        widget[0].visibility = attributes.getValue(i).trim();
//
//                                    break;
//                            }
//
////                            if (attributeQualifiedName != null && attributeQualifiedName.toLowerCase().equalsIgnoreCase("android:id")) {
////                                String viewId = attributes.getValue(i);
////                                //it is needed to remove prefixes android:id="@+id/btn_modulo"
////                                int index = viewId.lastIndexOf('/');
////                                if (index != -1) {
////                                    viewId = viewId.substring(index + 1).trim();
////                                }
////                                viewIds.add(viewId);
////                            }
//                        }
//                    }
//
//                    @Override
//                    public void endElement(String uri, String localName, String qName){
//                        if (parent[0].equalsIgnoreCase(qName)) { // Parent
//
//                        } else {
//                            if (widget[0].id != null) {
//                                if (widget[0].visibility == null)
//                                    widget[0].visibility = "visible";
//
//                                if (parentWidget[0].visibility.equalsIgnoreCase("visible"))
//                                    widgetList.add(widget[0]);
//                            }
//                        }
//
//                        widget[0] = new Widget();
//                    }
//                });
//
//
//            } catch (Exception e){
//                e.printStackTrace();
//                return null;
//            }
//        }
//
//        return widgetList;
//
//
//
//
//    }

    List<Widget> getViewAttr(Element element){

        List<Widget> widgets = new ArrayList<>();

        List<Element> views = element.getChildren();
        List<Attribute> attributes = element.getAttributes();

        Widget view = new Widget();
        for (int i = 0; i < attributes.size(); i++){
            String attName = element.getAttributes().get(i).getName().trim();
            switch (attName){
                case "id":
                    String viewId = element.getAttributes().get(i).getValue();
                    //it is needed to remove prefixes android:id="@+id/btn_modulo"
                    int index = viewId.lastIndexOf('/');
                    if (index != -1) {
                        viewId = viewId.substring(index + 1).trim();
                        view.id = viewId;
                    }
                    break;

                case "visibility":
                    view.visibility = element.getAttributes().get(i).getValue();
                    break;
            }
        }

        if (view.visibility == null)
            view.visibility = "visible";

        if (view.id != null){
            widgets.add(view);
        }

        if (view.visibility.equalsIgnoreCase("visible"))
            for (Element element1 : views){
                widgets.addAll(getViewAttr(element1));
            }

        return widgets;
    }

    List<Widget> getLayoutViews(VirtualFile layout) throws JDOMException, IOException {

        List<Widget> widgets = new ArrayList<>();

        SAXBuilder saxBuilder = new SAXBuilder();
        Document document = saxBuilder.build(String.valueOf(layout));

        Element rootView = document.getRootElement();

        widgets.addAll(getViewAttr(rootView));

        return widgets;
    }

    class Widget {

        public String id;
        public String visibility;

    }
}
