package fr.langladure.ld34;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import fr.langladure.ld34.screens.*;

public class GameBase extends Game {

	public static final String NAME = "GameBase";
	public static final String VERSION = "0.1.0";
	public static boolean RELEASE = false;
	public static boolean DEVMODE = false;

	public static Logger logger;

	public static StringBuilder stringBuilder;
	public static FreeTypeFontGenerator smallDebugGen;
	public static FreeTypeFontGenerator title2Gen;
	public static FreeTypeFontGenerator titleGen;

	public static FileHandleResolver resolver;
	public AssetsFinder assetsFinder;
	public SpriteBatch batch;
	public AssetManager assetManager;

	public MainMenuScreen mainMenuScreen;
	public OptionScreen optionScreen;
	public LoadingScreen loadingScreen;
	public GameScreen gameScreen;

	@Override
	public void create() {

		if (!RELEASE && Gdx.app.getType() == Application.ApplicationType.Desktop) {
			// Uncomment to add the images in the /images directory to the game asset's one
			FileHandle[] assetsFolder = Gdx.files.internal("../../_assets").list();
			for (FileHandle folder : assetsFolder) {
				if (folder.isDirectory() && !folder.name().startsWith("_")) {
//					TexturePacker.process("../../_assets/" + folder.name(), folder.name(), folder.name() + "Pack");
				}
			}
		}

		logger = new Logger(NAME, Application.LOG_DEBUG);

		Gdx.app.setLogLevel(Application.LOG_DEBUG);

		stringBuilder = new StringBuilder();

		batch = new SpriteBatch();

		resolver = new InternalFileHandleResolver();
		assetManager = new AssetManager(resolver);
		assetManager.setLogger(new Logger("AssetManager", Application.LOG_DEBUG));

		assetsFinder = new AssetsFinder(assetManager, resolver);

		Texture.setAssetManager(assetManager);

		assetManager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
		assetManager.setLoader(TiledMap.class, new TmxMapLoader(resolver));

		if (!assetsFinder.load("freetype")) log("Directory fonts doesn't exist");
		assetManager.finishLoading();

		log(resolver.resolve("freetype/Oxygen.ttf").toString());
		log(resolver.resolve("freetype/Oxygen.ttf").path());
		log(resolver.resolve("freetype/Oxygen.ttf").exists()+"");
		log(resolver.resolve("freetype").isDirectory()+"");
		log(resolver.resolve("freetype/").isDirectory()+"");

		smallDebugGen = assetManager.get("freetype/Oxygen.ttf", FreeTypeFontGenerator.class);
		titleGen = assetManager.get("freetype/Radley.ttf", FreeTypeFontGenerator.class);
		title2Gen = titleGen;

		mainMenuScreen = new MainMenuScreen(this);
		optionScreen = new OptionScreen(this);
		loadingScreen = new LoadingScreen(this);
		gameScreen = new GameScreen(this);

		loadingScreen.setNextScreen(mainMenuScreen);
		setScreen(loadingScreen);
	}

	@Override
	public void render() {
		super.render();
		if (!RELEASE && Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT) && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && Gdx.input.isKeyJustPressed(Input.Keys.D)) {
			DEVMODE = !DEVMODE;
		} else if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE) && screen != mainMenuScreen) {
			loadingScreen.setFadeWhenLoaded(true);
			loadingScreen.setNextScreen(mainMenuScreen);
			setScreen(loadingScreen);
		}
	}

	@Override
	public void dispose() {
		mainMenuScreen.dispose();
		loadingScreen.dispose();
		optionScreen.dispose();
		gameScreen.dispose();
		assetManager.dispose();
		batch.dispose();
	}

	@Override
	public void resume() {
		super.resume();
		if (Gdx.app.getType() == Application.ApplicationType.Android) {
			if (!screen.equals(loadingScreen))
				loadingScreen.setNextScreen((AbstractScreen) screen);
			setScreen(loadingScreen);
		}
	}

	public static void log(String message) {
		logger.info(message);
	}

	public static void debug(String message) {
		logger.debug(message);
	}
}
