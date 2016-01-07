package dirTree.util.recorder;

import dirTree.util.exception.*;
import dirTree.util.DirSystem;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.util.Set;

public class FileMoveBacker extends FileRecorder {
    //one good strategy is to only record those files instead of files and directories
    public FileMoveBacker(){
        super();
    }

    @Override
    public void addFile(File file){
        throw new NotImplementedException();
    }

    @Override
    public void addFile(File file, String orginalDir){
        if ( ! DirSystem.checkFile(orginalDir, true) )
            throw new IllegalArgumentException("Can't move back non-existing file");
        super.addFile(file, orginalDir);
    }

    public void moveBack(){
        Set<String> keySet = files.keySet();
        File fileStored = null;
        for ( String originalDir : keySet ){
            fileStored = files.get(originalDir);
            try {
                if (fileStored.isDirectory())
                    try {
                        DirSystem.moveDir(fileStored.getAbsolutePath(), originalDir);
                    } catch (InfoDirException ex) {
                        System.err.println("Moveback Error: " + ex.getMessage());
                    }
                else if (fileStored.isFile())
                    try {
                        DirSystem.moveFile(fileStored.getAbsolutePath(), originalDir);
                    } catch (InfoFileException ex) {
                        System.err.println("Moveback Error: " + ex.getMessage());
                    }
            } catch (SevereDirException | SevereFileException ex){
                System.err.println( "Moveback Error: " + ex.getMessage());
                System.err.println( "Movebacker keeps on" );
            }
        }
    }
}
