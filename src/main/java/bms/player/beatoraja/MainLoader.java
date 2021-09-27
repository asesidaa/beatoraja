package bms.player.beatoraja;

import bms.player.beatoraja.AudioConfig.DriverType;
import bms.player.beatoraja.launcher.PlayConfigurationView;
import bms.player.beatoraja.song.SQLiteSongDatabaseAccessor;
import bms.player.beatoraja.song.SongData;
import bms.player.beatoraja.song.SongDatabaseAccessor;
import bms.player.beatoraja.song.SongUtils;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Logger;

/**
 * 起動用クラス
 *
 * @author exch
 */
public class MainLoader extends Application {
	
	private static SongDatabaseAccessor songdb;
	
	private static final Set<String> illegalSongs = new HashSet<>();

	private static VersionChecker version;

    public static Lwjgl3Application app;

	public static void play(Path f, BMSPlayerMode auto, boolean forceExit, Config config, PlayerConfig player, boolean songUpdated) {
		if(config == null) {
			config = Config.read();			
		}

		for(SongData song : getScoreDatabaseAccessor().getSongDatas(SongUtils.illegalsongs)) {
			MainLoader.putIllegalSong(song.getSha256());
		}		
		if(illegalSongs.size() > 0) {
			JOptionPane.showMessageDialog(null, "This Application detects " + illegalSongs.size() + " illegal BMS songs. \n Remove them, update song database and restart.", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}

		try {
			MainController main = new MainController(f, config, player, auto, songUpdated);

			Lwjgl3ApplicationConfiguration cfg = new Lwjgl3ApplicationConfiguration();

			// refer to https://github.com/libgdx/libgdx/issues/4785
			switch (config.getDisplaymode()) {
				case FULLSCREEN:
					cfg.setAutoIconify(true);
                    cfg.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
					break;
				case BORDERLESS:
					var displayMode = Lwjgl3ApplicationConfiguration.getDisplayMode();
                    cfg.setWindowedMode(displayMode.width, displayMode.height);
                    cfg.setDecorated(false);
					break;
				case WINDOW:
					cfg.setWindowedMode(config.getResolution().width, config.getResolution().height);
					break;
                default:
                    throw new IllegalStateException("Unexpected value: " + config.getDisplaymode());
            }
			// vSync
            cfg.useVsync(config.isVsync());
            cfg.setForegroundFPS(config.getMaxFramePerSecond());
            cfg.setIdleFPS(config.getMaxFramePerSecond());
            cfg.setTitle(MainController.getVersion());

            cfg.setAudioConfig(config.getAudioConfig().getDeviceSimultaneousSources(),
                               config.getAudioConfig().getDeviceBufferSize(),
                               9);


			//cfg.forceExit = forceExit;
			if(config.getAudioConfig().getDriver() != DriverType.OpenAL) {
				cfg.disableAudio(true);
			}
			// System.setProperty("org.lwjgl.opengl.Display.allowSoftwareOpenGL",
			// "true");
			app = new Lwjgl3Application(main, cfg);

//			Lwjgl3ApplicationConfiguration cfg = new Lwjgl3ApplicationConfiguration();
//
//			final int w = (int) RESOLUTION[config.getResolution()].width;
//			final int h = (int) RESOLUTION[config.getResolution()].height;
//			if (config.isFullscreen()) {
//				DisplayMode d = null;
//				for (DisplayMode display : cfg.getDisplayModes()) {
//					System.out.println("available DisplayMode : w - " + display.width + " h - " + display.height
//							+ " refresh - " + display.refreshRate + " color bit - " + display.bitsPerPixel);
//					if (display.width == w
//							&& display.height == h
//							&& (d == null || (d.refreshRate <= display.refreshRate && d.bitsPerPixel <= display.bitsPerPixel))) {
//						d = display;
//					}
//				}
//				if (d != null) {
//					cfg.setFullscreenMode(d);
//				} else {
//					cfg.setWindowedMode(w, h);
//				}
//			} else {
//				cfg.setWindowedMode(w, h);
//			}
//			// vSync
//			cfg.useVsync(config.isVsync());
//			cfg.setIdleFPS(config.getMaxFramePerSecond());
//			cfg.setTitle(VERSION);
//
//			cfg.setAudioConfig(config.getAudioDeviceSimultaneousSources(), config.getAudioDeviceBufferSize(), 1);
//
//			new Lwjgl3Application(main, cfg);
		} catch (Throwable e) {
			e.printStackTrace();
			Logger.getGlobal().severe(e.getClass().getName() + " : " + e.getMessage());
		}
	}

	public static Graphics.DisplayMode[] getAvailableDisplayMode() {
		return Lwjgl3ApplicationConfiguration.getDisplayModes();
	}

	public static Graphics.DisplayMode getDesktopDisplayMode() {
		return Lwjgl3ApplicationConfiguration.getDisplayMode();
	}
	
	public static SongDatabaseAccessor getScoreDatabaseAccessor() {
		if(songdb == null) {
			try {
				Config config = Config.read();
				Class.forName("org.sqlite.JDBC");
				songdb = new SQLiteSongDatabaseAccessor(config.getSongpath(), config.getBmsroot());			
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return songdb;
	}

	public static VersionChecker getVersionChecker() {
		if(version == null) {
			version = new GithubVersionChecker();
		}
		return version;
	}

	public static void setVersionChecker(VersionChecker version) {
		if(version != null) {
			MainLoader.version = version;
		}
	}

	public static void putIllegalSong(String hash) {
		illegalSongs.add(hash);
	}
	
	public static String[] getIllegalSongs() {
		return illegalSongs.toArray(new String[illegalSongs.size()]);
	}
	
	public static int getIllegalSongCount() {
		return illegalSongs.size();
	}
	
	@Override
	public void start(javafx.stage.Stage primaryStage) throws Exception {
		Config config = Config.read();

		try {
//			final long t = System.currentTimeMillis();
			ResourceBundle bundle = ResourceBundle.getBundle("UIResources");
			FXMLLoader loader = new FXMLLoader(
                    MainLoader.class.getResource("/PlayConfigurationView.fxml"), bundle);
			VBox stackPane = (VBox) loader.load();
			PlayConfigurationView bmsinfo = (PlayConfigurationView) loader.getController();
			bmsinfo.setBMSInformationLoader(this);
			bmsinfo.update(config);
			Scene scene = new Scene(stackPane, stackPane.getPrefWidth(), stackPane.getPrefHeight());
			primaryStage.setScene(scene);
			primaryStage.setTitle(MainController.getVersion() + " configuration");
			primaryStage.setOnCloseRequest((event) -> {
				bmsinfo.exit();
			});
			primaryStage.show();
//			Logger.getGlobal().info("初期化時間(ms) : " + (System.currentTimeMillis() - t));

		} catch (IOException e) {
			Logger.getGlobal().severe(e.getMessage());
			e.printStackTrace();
		}
	}

	public interface VersionChecker {
		public String getMessage();
		public String getDownloadURL();
	}

	private static class GithubVersionChecker implements VersionChecker {

		private String dlurl;
		private String message;

		@Override
        public String getMessage() {
			if(message == null) {
				getInformation();
			}
			return message;
		}

		@Override
        public String getDownloadURL() {
			if(message == null) {
				getInformation();
			}
			return dlurl;
		}

		private void getInformation() {
			try {
				URL url = new URL("https://api.github.com/repos/exch-bms2/beatoraja/releases/latest");
				ObjectMapper mapper = new ObjectMapper();
				GithubLatestRelease lastestData = mapper.readValue(url, GithubLatestRelease.class);
				final String name = lastestData.name;
				if (MainController.getVersion().contains(name)) {
					message = "最新版を利用中です";
				} else {
					message = String.format("最新版[%s]を利用可能です。", name);
					dlurl = "https://mocha-repository.info/download/beatoraja" + name + ".zip";
				}
			} catch (Exception e) {
				Logger.getGlobal().warning("最新版URL取得時例外:" + e.getMessage());
				message = "バージョン情報を取得できませんでした";
			}
		}
	}

	@JsonIgnoreProperties(ignoreUnknown=true)
	static class GithubLatestRelease {
		public String name;
	}

}