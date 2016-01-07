package dirTree.util.recorder;

import dirTree.util.DirSystem;
import dirTree.util.recorder.FileRecorder;

import java.io.File;

public class FileCleanUper extends FileRecorder {
    public FileCleanUper(){
        super();
    }

    public void cleanUp(){
        for ( File file : files.values() ){
            if ( file.isDirectory() )
                DirSystem.deleteDir(file.getAbsolutePath());
            else if ( file.isFile() )
                DirSystem.deleteFile( file.getAbsolutePath() );
        }
    }
}
