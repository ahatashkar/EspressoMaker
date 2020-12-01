import org.junit.Rule;

public class ActivityNavigationStrategy implements TestStrategy {

    String ON_VIEW_CLICK = "onView(withId(R.id.[id])).perform(click());\n";
    String INTENDED_ACTIVITY = "intended(hasComponent([DestinationActivity].class.getName()));\n";
    String INTENT_TEST_RULE = "@Rule\npublic IntentsTestRule<[Activity]> mActivityIntentRule = new IntentsTestRule<>([Activity].class);\n\n";
    String ACTIVITY_SCENARIO_RULE = "@Rule\npublic ActivityScenarioRule<[Activity]> mActivityRule = new ActivityScenarioRule<>([Activity].class);\n\n";
    String SCENARIO_MoveToState = "ActivityScenario<[Activity]> scenario = mActivityRule.getScenario();\nscenario.moveToState(Lifecycle.State.DESTROYED);\n";



    @Override
    public String testGenerator(ActivityEntity entity) {

        StringBuilder testCode = new StringBuilder();

        if (entity.buttonNavigationInfoList.size() > 0) {
            testCode.append(INTENT_TEST_RULE.replace("[Activity]", entity.getJavaClass().getName()));
            testCode.append(ACTIVITY_SCENARIO_RULE.replace("[Activity]", entity.getJavaClass().getName()));
        }


        for (ButtonNavigationInfo button : entity.buttonNavigationInfoList){
            testCode.append(JavaCodeStrings.METHOD_HEADER.replace("[methodName]", "launch"+button.getNavigatedActivity().trim()+"Test"));
//            testCode.append(JavaCodeStrings.ACTIVITY_SCENARIO_LAUNCH.replace("[className]", entity.getJavaClass().getName()));

            testCode.append(ON_VIEW_CLICK.replace("[id]", button.getName()));

            if (button.getNavigatedActivity().equalsIgnoreCase("_finish")){
                testCode.append(SCENARIO_MoveToState.replace("[Activity]", entity.getJavaClass().getName()));


            } else {
                testCode.append(INTENDED_ACTIVITY.replace("[DestinationActivity]", button.getNavigatedActivity().trim()));
            }

            testCode.append(JavaCodeStrings.R_BRACKET);
        }


        return testCode.toString();
    }
}
