package termproject;

/**
 * Title:
 * Description:
 * Copyright:
 * Company:
 * @author
 * @version 1.0
 */


public class ElementNotFoundException extends RuntimeException {

    public ElementNotFoundException() {
        super ("Problem with TwoFourTree");
    }
    public ElementNotFoundException(String errorMsg) {
        super (errorMsg);
    }
}