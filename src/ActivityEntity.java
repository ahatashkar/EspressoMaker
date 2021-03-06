import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;

import java.util.ArrayList;
import java.util.List;

public class ActivityEntity {

    private VirtualFile layout;
    private PsiClass javaClass;

    private StaticCallback staticCallback;
    private DynamicCallback dynamicCallback;
    private AnnotatedCallback annotatedCallback;

    public List<ButtonNavigationInfo> buttonNavigationInfoList;

    public ActivityEntity(VirtualFile layout, PsiClass javaClass) {
        this.layout = layout;
        this.javaClass = javaClass;

        this.staticCallback = new StaticCallback(this);
        this.dynamicCallback = new DynamicCallback(this);
        this.annotatedCallback = new AnnotatedCallback();

        this.buttonNavigationInfoList = new ArrayList<>();
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

