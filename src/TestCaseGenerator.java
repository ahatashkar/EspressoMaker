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
    private String packageName;
//    private List<String> idList;

    public TestCaseGenerator(File file, ActivityEntity entity, String packageName){
        this.entity = entity;
        this.file = file;
        this.packageName = packageName;
        this.className = entity.getJavaClass().getName();
    }

    public void generate() {

//        idList = getRootLayoutId(entity.getLayout());

        try {

            FileWriter writer = new FileWriter(file);
            writer.write(JavaCodeStrings.PACKAGE_NAME);

            writer.write(JavaCodeStrings.IMPORTS.replace("[packageName]", packageName));
            writer.write("import " + packageName + "." + className + ";\n\n");

            writer.write(JavaCodeStrings.TEST_RUNNER);
            writer.write(JavaCodeStrings.CLASS_HEADER.replace("[className]", className));

            // test isActivityElementInView
            TestStrategy activityLaunchTest = new ActivityLaunch();
            writer.write(activityLaunchTest.testGenerator(entity));

//            writer.write(JavaCodeStrings.METHOD_HEADER.replace("[methodName]", "isActivityInView"));
//            writer.write(JavaCodeStrings.ACTIVITY_SCENARIO_LAUNCH.replace("[className]", className));
//            if(idList != null){
//                for(String id : idList)
//                    writer.write(JavaCodeStrings.ON_VIEW_CHECK.replace("[id]", id));
//            }
//            writer.write(JavaCodeStrings.R_BRACKET);






            writer.write(JavaCodeStrings.R_BRACKET);
            writer.close();

        } catch (Exception e){
            e.printStackTrace();
        }
    }








}
