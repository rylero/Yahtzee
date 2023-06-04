package Server;

import java.io.IOException;
import java.util.logging.*;

public class Logging {
    public static Logger logger;
    public Handler fileHandler;
    
    private Logging() throws IOException{
        logger = Logger.getLogger(Logging.class.getName());
        fileHandler = new FileHandler("./Server/Logs/log.txt", true);
        logger.addHandler(fileHandler);
    }

    private static Logger getLogger(){
        if(logger == null){
            try {
                new Logging();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return logger;
    }
    public static void log(Level level, String msg){
        getLogger().log(level, msg);
    }
}
