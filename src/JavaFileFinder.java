import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.jsp.JavaJspRecursiveElementVisitor;

import java.util.ArrayList;
import java.util.List;

public class JavaFileFinder {

    public static List<VirtualFile> getAllJavaFiles(VirtualFile directory) {
        VirtualFile[] children = directory.getChildren();
        List<VirtualFile> javaFiles = new ArrayList<>();

        for (VirtualFile child : children) {
            if (child.isDirectory()) {
                List<VirtualFile> innerJavaFiles = getAllJavaFiles(child);
                javaFiles.addAll(innerJavaFiles);

            } else {
                String childName = child.getName();
                if(childName.endsWith(".java")) {
                    javaFiles.add(child);
                }
            }
        }

        return javaFiles;
    }
}
