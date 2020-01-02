package Utilities;

/**
 * 
 * @author JAD4
 */
public class Constants {
    // BUTTON IMAGES
    public static final String DRAWLINEBUTTONIMAGE = "/Resources/btWall.jpg";
    public static final String DELETEBUTTONIMAGE = "/Resources/ic_delete.png";
    public static final String DRAWACCESPOINTBUTTONIMAGE = "/Resources/ic_ap.png";
    public static final String SAVEBUTTONIMAGE = "/Resources/save.png";
    public static final String LOADBUTTONIMAGE = "/Resources/load.png";
    public static final String CALCULATESIGNALBUTTONIMAGE = "/Resources/ic_wifi.png";
    // BUTTON TOOLTIPS
    public static final String DRAWLINEBUTTONTOOLTIP = "Draw Line";
    public static final String DELETEBUTTONTOOLTIP = "Delete";
    public static final String DRAWACCESPOINTBUTTONTOOLTIP = "Draw Access Point";
    public static final String SAVEBUTTONTOOLTIP = "Save";
    public static final String LOADBUTTONTOOLTIP = "Load";
    public static final String CALCULATESIGNALBUTTONTOOLTIP = "Calculate Signal";
    // LABELS
    public static final String WINDOWHEADER = "Drawing Tool";
    public static final String CLEARALLLABEL = "Clear All";
    // ERRORS
    public static final String SAVEFILEERROR = "The file is empty";
    public static final String LOADFILEERROR = "The file is empty";
    public static final String NOACCESSPOINTS = "There are no Access Points located on the plan!";
    //DEVICES IMAGES
    public static final String  LAPTOPIMAGE = "/Resources/laptop.png";
    public static final String  MOBILEIMAGE = "/Resources/mobile.png";
    
    public static final String  LAPTOP = "for Laptops";
    public static final String  MOBILE = "for Mobile phones";
    public static final String  OPTIMAL_LOCATION = "Optimal AP Location ";
    
    public static final String NEWAP_ERROR_MSG  = "Could not add AP Model";
    public static final String NEWAP_ERROR_TITLE = "Nothing Added!";
    public static final String NEWAP_SUCCESS_MSG = "Access Point Model added successfully";
    public static final String NEWAP_SUCCESS_TITLE = "AP Model Added!";
    
    public static final String NEW_MATERIAL_ERROR_MSG = "Could not add material";
    public static final String NEW_MATERIAL_ERROR_TITLE = "Nothing Added!";
    public static final String NEW_MATERIAL_SUCCESS_MSG = "Material added successfully";
    public static final String NEW_MATERIAL_SUCCESS_TITLE = "Material Added!";
    
    public static final String ERROR = "Error!";
    public static final String MISSING_FEILDS_MSG = "Some fields are missing";
    public static final String MISSING_FEILDS_TITLE = "Missing fields!";
    
    public static final String DEFAULT_AP_NAME = "Access Point ";
    public static final String NO_APS = "No access points";
    public static final String DBM = "dBm";
    public static final String GRID_QUESTION = "Would you like to draw a grid on the image ?";
    
    public static final String JSON_SETTINGS_FILE_NAME = "Settings.json";
    public static final String OPTIMAL_AP_NOT_FOUND = "No optimal location found. More than 1 AP is needed";
    public static final String OPTIMAL_AP_FOUND = "Optimal location found and the AP is set on the floor plan";
    
    public static final String SAVE_CHANGES_MSG = "Do you want to save changes of previous session ?";
    public static final String SAVE_CHANGES_TITLE = "Save Changes";
    public static final String WRONG_FILE_FORMAT = "Wrong file format!";
    
    public static final int DEFAULT_BRUTE_FORCE_STEP = 25; // pixels
    public static final int DEFAULT_HEAT_MAP_STEP = 3;// pixels
    public static final int DEFAULT_THICKNESS = 20; // cm
    public static final int DEFAULT_GRID_SIZE = 20; // pixels
    public static final int DEFAULT_DRAWING_SCALE = 50; // cm
    
    public static final int OPERATION_SELECT = 0;
    public static final int OPERATION_DRAW_LINE = 1;
    public static final int OPERATION_DRAW_AP = 2;
    public static final int OPERATION_CALC_SIGNAL = 3;
    
    public static final int MOVING_NOTHING = 0;
    public static final int MOVING_LINE = 1;
    public static final int MOVING_ENDPT = 2;
    public static final int MOVING_AP = 3;
    
    public static int HEAT_MAP_STEP = 3;
    public static int BRUTE_FORCE_STEP = 25;
    public static int DRAWING_SCALE = 50; // cm
    
    public static boolean isLaptop = false;
    public static boolean isChanged = false; // to save changes to file
}
