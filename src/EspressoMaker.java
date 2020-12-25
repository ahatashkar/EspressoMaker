import Utils.Utils;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class EspressoMaker implements Runnable {

    private Project project;
    private PsiElement psiElement;
    private List<PsiClass> projectJavaClasses;
    private List<VirtualFile> projectJavaFiles;

    private List<Entity> entityList;
//    private List<ActivityEntity> activityEntityList;
//    private List<FragmentEntity> fragmentEntityList;

    private VirtualFile sourceDirectory;
    String projectPackageName;

    public EspressoMaker(Project project, PsiElement psiElement){
        this.project = project;
        this.psiElement = psiElement;

        entityList = new ArrayList<>();
//        activityEntityList = new ArrayList<>();
//        fragmentEntityList = new ArrayList<>();
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

                File manifest = projectInformationExtractor.findManifestFile(new File(sourceDirectory.getCanonicalPath()));
                if(manifest != null){
                    Utils.showMessage("Android manifest file: " + manifest.getAbsolutePath());
                    ManifestAnalyzer manifestAnalyzer = new ManifestAnalyzer();
                    projectPackageName = manifestAnalyzer.getPackageName(manifest);
                    Utils.showMessage("Application class name: " + projectPackageName);
                }

                // detecting all activities
                for(VirtualFile layoutFile : layoutFiles){

                    String layoutName = layoutFile.getName();

                    for(PsiClass javaClass : projectJavaClasses){

                        if(isFragment(javaClass, layoutName)){
                            FragmentEntity fragment = new FragmentEntity(layoutFile, javaClass);
                            entityList.add(fragment);
                        }

                        else if(isActivity(javaClass, layoutName)){
                            ActivityEntity activity = new ActivityEntity(layoutFile, javaClass);
                            entityList.add(activity);
                        }
                    }

                }

                // creating test class for each activity
                File testDirectory = new File(sourceDirectory.getCanonicalPath(),
                        "androidTest" + File.separatorChar + "java" + File.separatorChar + "EspressoMaker");
                if(!testDirectory.exists()){
                    boolean isCreated = testDirectory.mkdir();
                    if(isCreated) {
                        Utils.showMessage("androidTest directory is created.");
                    } else
                        Utils.showMessage("Fail to create androidTest directory.");
                }

                if(testDirectory.exists()){

                    // Main Loop
                    for(Entity entity : entityList){
                        String activityName = entity.getJavaClass().getName();

                        entity.callbackDetection();

                        try {
                            File test = new File(sourceDirectory.getCanonicalPath(),
                                    "androidTest" + File.separatorChar + "java" + File.separatorChar + "EspressoMaker" + File.separatorChar + activityName + "Test.java");
                            if(test.createNewFile()){
                                Utils.showMessage(activityName+"Test is created");

                                TestCaseGenerator testCaseGenerator = new TestCaseGenerator(test, entity, projectPackageName);
                                testCaseGenerator.generate();



                            } else {
                                Utils.showMessage(activityName+"Test is failed to be created");
                            }


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
        if(!javaClass.getSuperClass().getName().equalsIgnoreCase("Fragment")) {
            ActivityFinder finder = new ActivityFinder(javaClass, layoutName);
            javaClass.accept(finder);
            return finder.isActivity();

        } else{
            return false;
        }
    }

    boolean isFragment(PsiClass javaClass, String layoutName){
        if (javaClass.getSuperClass().getName().equalsIgnoreCase("Fragment")) {
            FragmentFinder finder = new FragmentFinder(javaClass, layoutName);
            javaClass.accept(finder);
            return finder.isFragment();

        } else {
            return false;
        }
    }
}
