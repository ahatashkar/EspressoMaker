import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;

import java.util.ArrayList;

public class FragmentEntity extends Entity {

    public FragmentEntity(VirtualFile layout, PsiClass javaClass) {
        super(layout, javaClass);

        staticCallback = new StaticCallback(this);
        dynamicCallback = new DynamicCallback(this);
        annotatedCallback = new AnnotatedCallback();

        buttonNavigationInfoList = new ArrayList<>();
    }
}
