package dirTree.util.exception;

public class InfoDirException extends FileSystemException
    implements DirInvalidException, InfoException{
    public InfoDirException(String mess){
        super(mess);
    }
}
