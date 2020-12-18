import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;

import java.util.ArrayList;
import java.util.List;

public class ActivityEntity extends Entity{

    public ActivityEntity(VirtualFile layout, PsiClass javaClass) {
        super(layout, javaClass);

        staticCallback = new StaticCallback(this);
        dynamicCallback = new DynamicCallback(this);
        annotatedCallback = new AnnotatedCallback();

        buttonNavigationInfoList = new ArrayList<>();
    }


}

