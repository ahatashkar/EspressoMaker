import java.io.*;

public class TestCaseGenerator {

    private ActivityEntity entity;
    private File file;
    private String className;
    private String packageName;

    public TestCaseGenerator(File file, ActivityEntity entity, String packageName){
        this.entity = entity;
        this.file = file;
        this.packageName = packageName;
        this.className = entity.getJavaClass().getName();
    }

    public void generate() {

        try {

            FileWriter writer = new FileWriter(file);
            writer.write(JavaCodeStrings.PACKAGE_NAME);

            writer.write(JavaCodeStrings.IMPORTS.replace("[packageName]", packageName));
            writer.write("import " + packageName + "." + className + ";\n\n");

            writer.write(JavaCodeStrings.TEST_RUNNER);
            writer.write(JavaCodeStrings.CLASS_HEADER.replace("[className]", className));

            // Testing Activity in Isolation
            TestStrategy activityLaunchTest = new ActivityLaunchStrategy();
            writer.write(activityLaunchTest.testGenerator(entity));

            // Testing Activity Navigation
            TestStrategy activityNavigation = new ActivityNavigationStrategy();
            writer.write(activityNavigation.testGenerator(entity));



            writer.write(JavaCodeStrings.R_BRACKET);
            writer.close();

        } catch (Exception e){
            e.printStackTrace();
        }
    }








}
