import java.io.*;
import java.util.Scanner;

public class TestCaseGenerator {

    private String className;
    private File file;

    public TestCaseGenerator(File file, String className){
        this.className = className;
        this.file = file;
    }

    public void generate() {

        try {

            FileWriter writer = new FileWriter(file);
            writer.write("package EspressoMaker;\n");

            for(String str : Templates.getImports())
                writer.write(str + "\n");

            writer.write("@RunWith(AndroidJUnit4.class)\n");
            writer.write("public class " + className + "Test {\n");

            // test isActivityInView
            writer.write("@Test\n");
            writer.write("public void test_isActivityInView() {\n");
            writer.write("ActivityScenario.launch(" + className + ".class);\n");
            writer.write("onView(withId(R.id.main)).check(matches(isDisplayed()));\n");
            writer.write("}\n");






            writer.write("\n}");
            writer.close();

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
