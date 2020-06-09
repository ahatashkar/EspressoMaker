import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;

public class ActivityEntity {

    VirtualFile layout;
    PsiClass javaClass;

    public ActivityEntity(VirtualFile layout, PsiClass javaClass) {
        this.layout = layout;
        this.javaClass = javaClass;
    }

    public ActivityEntity(){}

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
