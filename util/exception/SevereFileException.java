package dirTree.util.exception;

public class SevereFileException extends FileSystemException
        implements SevereException, FileInvalidException{
    public SevereFileException(String mess){
        super(mess);
    }
}
