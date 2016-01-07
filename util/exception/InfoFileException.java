package dirTree.util.exception;

public class InfoFileException extends FileSystemException
    implements InfoException, FileInvalidException{
    public InfoFileException(String mess){
        super(mess);
    }
}
