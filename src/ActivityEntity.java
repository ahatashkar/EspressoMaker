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

    public List<ButtonHandler> buttonHandlers;

    public ActivityEntity(VirtualFile layout, PsiClass javaClass) {
        this.layout = layout;
        this.javaClass = javaClass;

        this.staticCallback = new StaticCallback(this);
        this.dynamicCallback = new DynamicCallback();
        this.annotatedCallback = new AnnotatedCallback();

        this.buttonHandlers = new ArrayList<>();
    }

    public void callbackDetection(){
        staticCallback.getCallbacks();

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

