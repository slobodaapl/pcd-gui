
package pcd.utils;

/**
 *
 * SERVER_DEBUG - Disables Python connection when set to 'true'. Still runs it.
 * PROCESS_DEBUG - Prevent Python from running, useful for annotation-only-mode.
 * 
 */
public class Constant {

    public static final double SCORE_THRESHOLD = 0.75;
    public static final double FILTER_THRESHOLD_DISTANCE = 70;
    public static final boolean PROCESS_DEBUG = true;
    public static final boolean SERVER_DEBUG = true;
    public static final boolean ONLY_NORMAL = true;

    public static final String CONFIG_PATH = "celltypes_config.conf";
    
    public static final String INFERENCE_SERVER_STRING = "infer;";
    public static final String ANGLE_SERVER_STRING = "angle;";
    
    public static final int SERVER_PORT = 61387;

}
