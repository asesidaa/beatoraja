package bms.player.beatoraja;

import bms.player.beatoraja.ir.IRConnectionManager;

import javax.swing.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class MainLauncher {

    private static final boolean ALLOWS_32BIT_JAVA = false;

    private static Path bmsPath;


    public static void main(String[] args) {

        if(!ALLOWS_32BIT_JAVA && !System.getProperty( "os.arch" ).contains( "64")) {
            JOptionPane.showMessageDialog(null, "This Application needs 64bit-Java.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        Logger logger = Logger.getGlobal();
        try {
            logger.addHandler(new FileHandler("beatoraja_log.xml"));
        } catch (Throwable e) {
            e.printStackTrace();
        }

        BMSPlayerMode auto = null;
        for (String s : args) {
            if (s.startsWith("-")) {
                if (s.equals("-a")) {
                    auto = BMSPlayerMode.AUTOPLAY;
                }
                if (s.equals("-p")) {
                    auto = BMSPlayerMode.PRACTICE;
                }
                if (s.equals("-r") || s.equals("-r1")) {
                    auto = BMSPlayerMode.REPLAY_1;
                }
                if (s.equals("-r2")) {
                    auto = BMSPlayerMode.REPLAY_2;
                }
                if (s.equals("-r3")) {
                    auto = BMSPlayerMode.REPLAY_3;
                }
                if (s.equals("-r4")) {
                    auto = BMSPlayerMode.REPLAY_4;
                }
                if (s.equals("-s")) {
                    auto = BMSPlayerMode.PLAY;
                }
            } else {
                bmsPath = Paths.get(s);
                if(auto == null) {
                    auto = BMSPlayerMode.PLAY;
                }
            }
        }

        if(Files.exists(MainController.configpath) && (bmsPath != null || auto != null)) {
            IRConnectionManager.getAllAvailableIRConnectionName();
            MainLoader.play(bmsPath, auto, true, null, null, bmsPath != null);
        } else {
            MainLoader.launch(MainLoader.class,args);
        }
    }
}
