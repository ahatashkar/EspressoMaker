import Utils.Utils;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EspressoMaker implements Runnable {

    private Project project;
    private PsiElement psiElement;
    private List<PsiClass> projectJavaClasses;
    private List<VirtualFile> projectJavaFiles;
    private List<ActivityEntity> activityEntityList;

    private VirtualFile sourceDirectory;

    public EspressoMaker(Project project, PsiElement psiElement){
        this.project = project;
        this.psiElement = psiElement;
        activityEntityList = new ArrayList<>();
    }

    @Override
    public void run() {

        Utils.showMessage("Start processing project: " + project.getName());

        if(project.getBasePath() != null) {
            VirtualFile baseDirectory = LocalFileSystem.getInstance().findFileByPath(project.getBasePath());
            if(baseDirectory != null) {
                Utils.showMessage(baseDirectory.getPath());

                ProjectInformationExtractor projectInformationExtractor = new ProjectInformationExtractor(psiElement);
                this.sourceDirectory = projectInformationExtractor.getSourceDirectory(baseDirectory);
                this.projectJavaClasses = projectInformationExtractor.getProjectJavaClasses();
                this.projectJavaFiles = JavaFileFinder.getAllJavaFiles(sourceDirectory);

                List<VirtualFile> layoutFiles = projectInformationExtractor.getLayoutXMLFiles(baseDirectory);

                // detecting all activities
                for(VirtualFile layoutFile : layoutFiles){

                    String layoutName = layoutFile.getName();

                    for(PsiClass javaClass : projectJavaClasses){
                        if(isActivity(javaClass, layoutName)){
                            ActivityEntity activity = new ActivityEntity(layoutFile, javaClass);
                            activityEntityList.add(activity);
                        }
                    }

                    if(layoutFile.getCanonicalPath() != null) {
                        File xmlFile = new File(layoutFile.getCanonicalPath());
                    }



                }

                // creating test class for each activity
                File testDirectory = new File(sourceDirectory.getCanonicalPath(), "androidTest/java/EspressoMaker");
                if(!testDirectory.exists()){
                    boolean isCreated = testDirectory.mkdir();
                    if(isCreated) {
                        Utils.showMessage("androidTest directory is created.");
                    } else
                        Utils.showMessage("Fail to create androidTest directory.");
                }

                if(testDirectory.exists()){

                    for(ActivityEntity entity : activityEntityList){
                        String activityName = entity.getJavaClass().getName();

                        try {
                            File test = new File(sourceDirectory.getCanonicalPath(), "androidTest/java/EspressoMaker/" + activityName + "Test.java");
                            if(test.createNewFile()){
                                Utils.showMessage(activityName+"Test is created");
                            } else {
                                Utils.showMessage(activityName+"Test is failed to be created");
                            }

//                        FileOutputStream fileOutputStream = new FileOutputStream(test);
//                        fileOutputStream.close();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                }

                Utils.showMessage("Finished!");







            }
        }

    }

    boolean isActivity(PsiClass javaClass, String layoutName){
        ActivityFinder finder = new ActivityFinder(javaClass, layoutName);
        javaClass.accept(finder);
        return finder.isActivity();
    }
}
