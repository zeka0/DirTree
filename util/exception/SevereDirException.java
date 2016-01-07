package dirTree.util.exception;

public class SevereDirException extends FileSystemException
        implements SevereException, DirInvalidException{
    public SevereDirException(String mess){
        super(mess);
    }
}
