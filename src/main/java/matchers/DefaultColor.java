package matchers;

import beans.TrelloBoardLabel;
import constants.LabelColour;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import java.util.Set;

public class DefaultColor extends TypeSafeMatcher<TrelloBoardLabel> {

    private TrelloBoardLabel label;

    @Override
    protected boolean matchesSafely(TrelloBoardLabel label) {


        Set<String> defaultColorSet = Set.of(LabelColour.GREEN.colour, LabelColour.YELLOW.colour,
                LabelColour.ORANGE.colour, LabelColour.RED.colour,
                LabelColour.PURPLE.colour, LabelColour.BLUE.colour);


        if (defaultColorSet.contains(label.getColor())) {
            return true;
        }
        return false;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("one of default Colors");
    }

    public static Matcher<TrelloBoardLabel> defaultColor() {
        return new DefaultColor();
    }
}
