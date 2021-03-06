import Utils.Utils;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProjectInformationExtractor {
    private PsiElement projectElement;

    public ProjectInformationExtractor(PsiElement projectElement) {
        this.projectElement = projectElement;
    }

    public List<PsiClass> getProjectJavaClasses() {
        JavaClassFinder javaClassFinder = new JavaClassFinder();
        projectElement.accept(javaClassFinder);
        return javaClassFinder.getJavaClasses();
    }

    public List<VirtualFile> getLayoutXMLFiles(VirtualFile projectBaseDirectory) {
        List<VirtualFile> resultOnFailure = new ArrayList<>();
        VirtualFile srcDirectory = getSourceDirectory(projectBaseDirectory);
        if (srcDirectory == null) {
            Utils.showMessage("Failed to detect source directory.");
            return resultOnFailure;
        }
        Utils.showMessage("srcDirectory: " + srcDirectory.getCanonicalPath());
        VirtualFile mainDirectory = getMainDirectory(srcDirectory);
        if (mainDirectory == null) {
            Utils.showMessage("Failed to detect main directory.");
            return resultOnFailure;
        }
        Utils.showMessage("mainDirectory: " + mainDirectory.getCanonicalPath());
        VirtualFile resourcesDirectory = getResourcesDirectory(mainDirectory);
        if (resourcesDirectory == null) {
            Utils.showMessage("Failed to detect resources directory.");
            return resultOnFailure;
        }
        Utils.showMessage("resourcesDirectory: " + resourcesDirectory.getCanonicalPath());
        VirtualFile layoutDirectory = getLayoutDirectory(resourcesDirectory);
        if (layoutDirectory == null) {
            Utils.showMessage("Failed to detect layouts directory.");
            return resultOnFailure;
        }
        Utils.showMessage("layoutDirectory: " + layoutDirectory.getCanonicalPath());
        LayoutFinder layoutFinder = new LayoutFinder();
        return layoutFinder.getLayouts(layoutDirectory);
    }

    public VirtualFile getSourceDirectory(VirtualFile directory) {

        return getChildDirectory("src", directory, true);
    }

    private VirtualFile getMainDirectory(VirtualFile directory) {
        return getChildDirectory("main", directory, false);
    }

    private VirtualFile getResourcesDirectory(VirtualFile directory) {
        return getChildDirectory("res", directory, true);
    }

    private VirtualFile getLayoutDirectory(VirtualFile directory) {
        return getChildDirectory("layout", directory, true);
    }

    private boolean hasAndroidManifest(VirtualFile directory) {
        boolean result = false;
        VirtualFile[] children = directory.getChildren();
        search:
        for (VirtualFile child : children) {
            if (child.isDirectory()) {
                VirtualFile[] children2 = child.getChildren();
                for (VirtualFile child2 : children2) {
                    if (child2.getName().equals("AndroidManifest.xml")) {
                        result = true;
                        break;
                    }
                }
            }
        }
        return result;
    }

    private VirtualFile getChildDirectory(String childDirectoryName, VirtualFile parentDirectory, boolean depthFirst) {
        VirtualFile result = null;
        VirtualFile[] children = parentDirectory.getChildren();
        for (VirtualFile child : children) {
            if (child.isDirectory()) {
                if (child.getName().equals(childDirectoryName)) {
                    if (childDirectoryName.equals("src")) {
                        if (containsSubDirectories(child, "main", "androidTest")
                                || containsSubDirectories(child, "main", "test")
                                || (containsSubDirectories(child, "main") && hasAndroidManifest(child))) {
                            result = child;
                            break;
                        }
                    } else {
                        result = child;
                        break;
                    }
                } else if (depthFirst) {
                    VirtualFile temp = getChildDirectory(childDirectoryName, child, depthFirst);
                    if (temp != null) {
                        result = temp;
                        break;
                    }
                }
            }
        }
        return result;
    }

    private boolean containsSubDirectories(VirtualFile directory, String... subDirectoryNames) {
        boolean result = true;
        VirtualFile[] children = directory.getChildren();
        for (String subDirectoryName : subDirectoryNames) {
            boolean exists = false;
            for (VirtualFile child : children) {
                if (child.isDirectory() && child.getName().equals(subDirectoryName)) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                result = false;
                break;
            }
        }
        return result;
    }

    public File findManifestFile(File sourceDirectory) {
        File manifestFile = new File(sourceDirectory, "main" + File.separatorChar + "AndroidManifest.xml");
        if (manifestFile.exists() && manifestFile.isFile()) {
            return manifestFile;
        } else {
            return null;
        }
    }
}
