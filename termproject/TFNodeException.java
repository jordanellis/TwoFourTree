package termproject;

/**
 * Title:        Term Project 2-4 Trees
 * Description:
 * Copyright:    
 * Company:
 * @author
 * @version 1.0
 */

public class TFNodeException extends RuntimeException {

    public TFNodeException() {
        super ("Problem with TFNode");
    }
    public TFNodeException(String errorMsg) {
        super (errorMsg);
    }
}
