package GUI.Util;

import GUI.MainInterface.MainInterface;

/**
 *
 * @author Carlos Roberto
 */
public class MainInterfaceHandler {
    private static MainInterface mainInterface;
    
    public static void setMainInterface(MainInterface mInterface){
        mainInterface = mInterface;
    }
    
    public static MainInterface getMainInterface(){
        return mainInterface;
    }
}
