import java.util.List;

public class Templates {

    public static String[] getImports(){
        return new String[]{
                "import org.junit.Test;",
                "import org.junit.runner.RunWith;",
                "import androidx.test.core.app.ActivityScenario;",
                "import androidx.test.espresso.matcher.ViewMatchers;",
                "import androidx.test.ext.junit.runners.AndroidJUnit4;",
                "import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;",
                "import static androidx.test.espresso.Espresso.onView;",
                "import static androidx.test.espresso.action.ViewActions.typeText;",
                "import static androidx.test.espresso.assertion.ViewAssertions.matches;",
                "import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;",
                "import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;",
                "import static androidx.test.espresso.matcher.ViewMatchers.withId;",
                "import static androidx.test.espresso.matcher.ViewMatchers.withText;",
                "import static org.junit.Assert.*;"
        };

    }
}
