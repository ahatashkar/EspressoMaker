import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;

import java.util.List;

public class Entity {

    VirtualFile layout;
    PsiClass javaClass;

    StaticCallback staticCallback;
    DynamicCallback dynamicCallback;
    AnnotatedCallback annotatedCallback;

    List<ButtonNavigationInfo> buttonNavigationInfoList;

    public Entity(VirtualFile layout, PsiClass javaClass) {
        this.layout = layout;
        this.javaClass = javaClass;
    }

    public void callbackDetection(){
        staticCallback.getCallbacks();
        dynamicCallback.getCallbacks();

        //TODO: implement annotatedCallback detection
        int i = 0;

    }

    public VirtualFile getLayout() {
        return layout;
    }

    public void setLayout(VirtualFile layout) {
        this.layout = layout;
    }

    public PsiClass getJavaClass() {
        return javaClass;
    }

    public void setJavaClass(PsiClass javaClass) {
        this.javaClass = javaClass;
    }

}
