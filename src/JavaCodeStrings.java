import org.junit.Before;

public class JavaCodeStrings {

    public static final String PACKAGE_NAME = "package EspressoMaker;\n\n";
    public static final String IMPORTS =
            "import org.junit.Rule;\n"+
            "import org.junit.Test;\n"+
            "import org.junit.Before;\n"+
            "import org.junit.runner.RunWith;\n"+
            "import androidx.lifecycle.Lifecycle;\n"+
            "import androidx.test.core.app.ActivityScenario;\n"+
            "import androidx.test.espresso.intent.rule.IntentsTestRule;\n"+
            "import androidx.test.espresso.matcher.ViewMatchers;\n"+
            "import androidx.test.ext.junit.runners.AndroidJUnit4;\n"+
            "import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;\n"+
            "import static androidx.test.espresso.Espresso.onView;\n"+
            "import androidx.test.ext.junit.rules.ActivityScenarioRule;\n"+
            "import static androidx.test.espresso.action.ViewActions.typeText;\n"+
            "import static androidx.test.espresso.assertion.ViewAssertions.matches;\n"+
            "import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;\n"+
            "import static androidx.test.espresso.action.ViewActions.click;\n"+
            "import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;\n"+
            "import static androidx.test.espresso.matcher.ViewMatchers.withId;\n"+
            "import static androidx.test.espresso.matcher.ViewMatchers.withText;\n"+
            "import static androidx.test.espresso.intent.Intents.intended;\n" +
            "import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;\n"+
            "import static org.junit.Assert.*;\n"+
            "import [packageName].R;\n";


    public static final String TEST_RUNNER = "@RunWith(AndroidJUnit4.class)\n";

    public static final String CLASS_HEADER = "public class [className]Test {\n\n";
    public static final String METHOD_HEADER = "@Test\n"+ "public void test_[methodName]() {\n";

    public static final String ACTIVITY_SCENARIO_LAUNCH = "@Before\npublic void initial(){\nActivityScenario.launch([className].class);\n}\n\n";

    public static final String R_BRACKET = "\n}\n\n";
}
