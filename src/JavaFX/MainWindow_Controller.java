package JavaFX;

import commandline.FinishTime;
import commandline.Grabber;
import commandline.Movie;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import utils.Debugger;
import utils.Settings;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.regex.Pattern;

import static commandline.Grabber.*;
import static utils.Debugger.writeerror;

/**
 * Project: MovieBlogLookup
 * Package: JavaFX
 * Created by keno on 04.10.18.
 */
public class MainWindow_Controller {
	
	@FXML
	private TableView<Movie> maintable;
	@FXML
	private ImageView image_artwork;
	@FXML
	private AnchorPane ap_table, ap_details;
	@FXML
	private TextArea ta_downlink;
	@FXML
	private Hyperlink hl_op;
	@FXML
	private Button btn_cat1,btn_cat2,btn_cat3,btn_cat4,btn_cat5;
	@FXML
	private TextField tf_searchbar;
	
	private String colNames[] = {"Title","Jahr","Auflösung","Länge (Min.)","Codec","3D","Dateigröße (MB)"};
	private String colVarNames[] = {"Name","Year","Dimension","Duration","Codec","DreiD","FileSize"};
	/**
	 * The Col sizes.
	 */
	int colSizes[] = {550,50,100,100,70,50,150};
	private TableColumn cols[] = new TableColumn[7];
	/**
	 * The Table content.
	 */
	public static ObservableList<Movie> tableContent = FXCollections.observableArrayList();
	/**
	 * The constant Movies.
	 */
	public static ArrayList<Movie> Movies = new ArrayList<>();
	private static ArrayList<Movie> Searchresults = new ArrayList<>();
	/**
	 * The Main website.
	 */
	String MainWebsites[] = {"http://movie-blog.org/category/"/*,"http://hd-world.org/category/"*/};
	/**
	 * The Categorys.
	 */
	public static String[] Categorys = {"neuerscheinungen","top-releases","hd","hd-serien","4kuhd"};
	public static String[][] CategoryPaths = {{"neuerscheinungen/neu1080p","top-releases","hd","hd-serien","4kuhd"}/*,{"neuerscheinungen","top-releases","1080p",null,"2160p"}*/};
	/**
	 * The constant cur_Category.
	 */
	public static int cur_Category;
	private static String lastSearch = "";
	/**
	 * The Buttons.
	 */
	Button Buttons[];

	/**
	 * The Zeit.
	 */
	FinishTime Zeit = Grabber.Zeit;

	
	@FXML
	private void initialize(){
		
		SplitPane.setResizableWithParent(ap_table,false);
		SplitPane.setResizableWithParent(ap_details,false);
		
		hl_op.setDisable(true);
		
		try{
			image_artwork.setImage(new Image(String.valueOf(ClassLoader.getSystemResource("images/nocover.png"))));
		}catch(Exception e){
			writeerror(e);
			System.exit(2);
		}
		
		for (int i = 0; i < cols.length;i++){
			cols[i] = new TableColumn();
			cols[i].setText(colNames[i]);
			cols[i].setMinWidth(colSizes[i]);
			cols[i].setMaxWidth(colSizes[i]);
			cols[i].setSortable(true);
			if (i != 0)cols[i].setStyle("-fx-alignment: CENTER;");
			cols[i].setCellValueFactory(new PropertyValueFactory<>(colVarNames[i]));
		}

		//TODO Soll auch funktionieren wenn Tabelle sortiert wird
		/*
		cols[0].setCellFactory(column -> {
			return new TableCell<Movie, String>() {
				@Override
				protected void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);

					setText(empty ? "" : getItem());
					setGraphic(null);

					TableRow<Movie> currentRow = getTableRow();

					ArrayList<String> Links = new ArrayList<>();
					try {
						Links = currentRow.getItem().getLink();

						if (Links != null) {
							if (Links.size() == 0) {
								currentRow.setStyle("-fx-background-color:red");
							}else
								currentRow.setStyle("-fx-background-color: transparent");
						}else {
							currentRow.setStyle("-fx-background-color: transparent");
						}
					}catch(NullPointerException e){
						currentRow.setStyle("-fx-background-color: transparent");
					}


				}
			};
		});
		*/
		
		maintable.setRowFactory(tv ->{
			TableRow<Movie> row = new TableRow<>();
			row.setOnKeyPressed(event -> {
				System.out.println(event.getSource());
				if (event.getCode() == KeyCode.DOWN || event.getCode() == KeyCode.UP){
					showDetailsforMovie(maintable.getSelectionModel().getSelectedItem());

				}
			});
			row.setOnMouseClicked(event -> {
				if (! row.isEmpty() && event.getButton()== MouseButton.PRIMARY) {
					if (event.getClickCount() == 1) {
						Movie Film = row.getItem();
						showDetailsforMovie(Film);
					}
				}
			});
			return row ;
		});
		
		Buttons = new Button[]{btn_cat1,btn_cat2,btn_cat3,btn_cat4,btn_cat5};
		for (int i = 0; i < Buttons.length;i++){
			Buttons[i].setOnMouseClicked(event -> {
				if (event.getClickCount() == 2){
					switch (getValueforKey(event.getSource().toString(),"id")){
						case "btn_cat1":{
							showCategory(0,false);
							break;
						}
						case "btn_cat2":{
							showCategory(1,false);
							break;
						}
						case "btn_cat3":{
							showCategory(2,false);
							break;
						}
						case "btn_cat4":{
							showCategory(3,false);
							break;
						}
						case "btn_cat5":{
							showCategory(4,false);
							break;
						}
					}
				}else
					if (event.getClickCount() == 1){
						switch (getValueforKey(event.getSource().toString(),"id")){
							case "btn_cat1":{
								showCategory(0);
								break;
							}
							case "btn_cat2":{
								showCategory(1);
								break;
							}
							case "btn_cat3":{
								showCategory(2);
								break;
							}
							case "btn_cat4":{
								showCategory(3);
								break;
							}
							case "btn_cat5":{
								showCategory(4);
								break;
							}
						}
					}
			});
		}
		Grabber.task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				return null;
			}
		};
		
		tf_searchbar.textProperty().addListener((observable, oldValue, newValue) -> {
			if (Searchresults == null || (oldValue.length() > newValue.length()) || newValue.equals("")){
				Searchresults = new ArrayList<>();
				Searchresults.addAll(Movies);
				
			}
			String search[] = newValue.split(Pattern.quote(" "));
			for (int index = Searchresults.size() -1; index >= 0;index--){
				String Title = Searchresults.get(index).getOPTitle();
				boolean in = true;
				for (int arg = 0; arg < search.length;arg++){
					if (!Title.toLowerCase().contains(search[arg].toLowerCase())){
						in = false;
					}
				}
				if (!in)Searchresults.remove(index);
			}
			updateTableContent(Searchresults);
			//lastSearch = newValue;
		});
		
		
		//Zeigt die Standardmaessige Category beim starten des Programms an
		maintable.getColumns().addAll(cols);
		showCategory(1);
	}
	
	private void highlightButton(int index, Button buttons[]){
		for (int i = 0; i < buttons.length;i++){
			if (i == index){
				buttons[i].setStyle("-fx-text-fill: orange;");
			}else{
				buttons[i].setStyle("-fx-text-fill: white;");
			}
		}
	}
	
	private void showCategory(int index){
		showCategory(index,true);
	}
	
	private void showCategory(int index, boolean getfromOfflineStorage) {
		Platform.runLater(() -> {
			if (index >= 0 && index < Categorys.length) {
				String Category = Categorys[index];
				File startList = new File(Category + ".list");
				ArrayList<Movie> Filme = null;
				if (getfromOfflineStorage) Filme = loadMovieList(startList);
				if (Filme == null) {
					Alert alert;
					if (getfromOfflineStorage) {
						Debugger.Sout("showCategory -> Failed to Load Offline storage. Trying to re-download");
						alert = new Alert(Alert.AlertType.WARNING, "Es konnten keine Filme aus dem Offline-Speicher geladen werden!\nEs wird jetzt versucht eine neue zu erstellen. Dies kann zwischen 10 Sek. und 30Min. dauern.\n\nFortfahren?", ButtonType.YES, ButtonType.NO);
					} else {
						Debugger.Sout("showCategory -> Started manual re-download");
						alert = new Alert(Alert.AlertType.CONFIRMATION, "Manueller listen Download gestartet!\n Dies kann zwischen 10 Sek. und 30Min. dauern.\n\nFortfahren?", ButtonType.YES, ButtonType.NO);
					}
					alert.showAndWait();
					if (alert.getResult() == ButtonType.YES) {
						String[] paths = new String[MainWebsites.length];
						for (int i = 0; i < MainWebsites.length; i++) {
							paths[i] = CategoryPaths[i][index];
						}
						updateMovies(MainWebsites, paths, 15);
						MainWindow_Controller.tableContent = FXCollections.observableArrayList();
						maintable.setItems(tableContent);
						Grabber.task.setOnSucceeded(event -> {
							Movies = Grabber.Movies;
							pForm.getDialogStage().close();
							MainWindow_Controller.tableContent = FXCollections.observableArrayList();
							MainWindow_Controller.tableContent.addAll(Movies);
							maintable.setItems(tableContent);
							if (Movies.size() > 0) storeMovieList(Movies, new File(Categorys[cur_Category] + ".list"));
						});
						showDetailContent(index);
					} else {
						Debugger.Sout("showCategory -> Re-Download canceled!");
					}
				} else {
					Movies = Filme;
					Collections.sort(Movies.subList(1, Movies.size()), Comparator.comparing(Movie::getOPTitle));
					updateTableContent(Movies);
					showDetailContent(index);
				}

			}
		});

	}

	private void showDetailContent(int index) {
		cur_Category = index;
		Debugger.Sout("showCategory -> Loading successfully!");
		tf_searchbar.setText("");
		Searchresults = null;
		highlightButton(index,Buttons);
		ta_downlink.setText("");
		hl_op.setDisable(true);
		try{
			image_artwork.setImage(new Image(String.valueOf(ClassLoader.getSystemResource("images/" + Categorys[index] + ".png"))));
		}catch(Exception e){
			writeerror(e);
			image_artwork.setImage(new Image(String.valueOf(ClassLoader.getSystemResource("images/nocover.png"))));
		}
	}

	/**
	 * Store movie list boolean.
	 *
	 * @param Filme   the filme
	 * @param storage the storage
	 * @return the boolean
	 */
	public static boolean storeMovieList(ArrayList<Movie> Filme, File storage){
		Debugger.Sout("storeMovieList -> Trying to Store MovieList " + storage.getName());
		Settings loadedMovies= new Settings(storage,false);
		try{
			loadedMovies.addProperty("anzMovies",Filme.size()+"");
			loadedMovies.addProperty("date",new Date().getTime()+"");
			for(int i = 0; i < Filme.size();i++){
				String act = Debugger.equalLenghts(i, Filme.size(),"0");
				loadedMovies.addProperty(act+"01",Filme.get(i).getName());
				loadedMovies.addProperty(act+"02",Filme.get(i).getOPTitle());
				loadedMovies.addProperty(act+"03",Filme.get(i).getYear() + "");
				loadedMovies.addProperty(act+"04",Filme.get(i).isDreiD()+"");
				loadedMovies.addProperty(act+"05",Filme.get(i).getDimension().name());
				loadedMovies.addProperty(act+"06",Filme.get(i).getCodec().name());
				ArrayList<String> links = Filme.get(i).getLink();
				loadedMovies.addProperty(act+"07",links.size()+"");
				for (int link = 0; link < links.size();link++){
					loadedMovies.addProperty(act+"07"+Debugger.equalLenghts(link,links.size(),"0"),links.get(link));
				}
				loadedMovies.addProperty(act+"08",Filme.get(i).getPostLink());
				loadedMovies.addProperty(act+"09",Filme.get(i).getImageLink());
				loadedMovies.addProperty(act+"10",Filme.get(i).getFileSize()+"");
				loadedMovies.addProperty(act+"11",Filme.get(i).getDuration()+"");
				
			}
			//Serializeable
			ObjectOutputStream bw = new ObjectOutputStream(new FileOutputStream(new File(storage.getAbsoluteFile() + ".ser")));
			bw.writeObject(Filme);
			bw.close();


		}catch (Exception e){
			Debugger.writeerror(e);
			Debugger.Sout("storeMovieList -> Failed to Store MovieList " + storage.getName());
			return false;
		}
		boolean succes =loadedMovies.saveProperties();
		if (succes){
			Debugger.Sout("storeMovieList -> Stored MovieList " + storage.getName());
		}else{
			Debugger.Sout("storeMovieList -> Failed to Store MovieList " + storage.getName());
		}
		return succes;
	}

	/**
	 * Load movie list array list.
	 *
	 * @param List the list
	 * @return the array list
	 */
	public static ArrayList<Movie> loadMovieList(File List){
		ArrayList<Movie> Filme = new ArrayList<>();
		if (List.exists()){
			try{
				Settings loadedMovies = new Settings(List,false);
				if (loadedMovies.loadProperties()){
					int anzMovies = Integer.parseInt(loadedMovies.getProperty("anzMovies"));
					for (int i = 0; i < anzMovies;i++){
						String act = Debugger.equalLenghts(i,anzMovies,"0");
						String name = loadedMovies.getProperty(act+"01");
						String optitle = loadedMovies.getProperty(act+"02");
						int year = Integer.parseInt(loadedMovies.getProperty(act+"03"));
						boolean dreiD = Boolean.parseBoolean(loadedMovies.getProperty(act+"04"));
						String temp = loadedMovies.getProperty(act+"05");
						Movie.Quality dimension = (temp.equals(Movie.Quality.FULLHD.name()) ? Movie.Quality.FULLHD: (temp.equals(Movie.Quality.UHD.name()) ? Movie.Quality.UHD: Movie.Quality.HDREADY));
						temp = loadedMovies.getProperty(act + "06");
						Movie.Codecs codec = (temp.equals(Movie.Codecs.H265.name()) ? Movie.Codecs.H265: Movie.Codecs.H264);
						int anz_links = Integer.parseInt(loadedMovies.getProperty(act + "07"));
						ArrayList<String> links = new ArrayList<>();
						for (int link = 0; link < anz_links;link++){
							links.add(loadedMovies.getProperty(act + "07" + Debugger.equalLenghts(link,anz_links,"0")));
						}
						String postlink = loadedMovies.getProperty(act + "08");
						String imagelink = loadedMovies.getProperty(act + "09");
						int filesize = Integer.parseInt(loadedMovies.getProperty(act + "10"));
						int duration = Integer.parseInt(loadedMovies.getProperty(act + "11"));
						if (!links.isEmpty())Filme.add(new Movie(name,optitle,year,dreiD,dimension,codec,links,postlink,imagelink,filesize,duration));
					}
				}else
					return null;
				
			}catch (Exception e){
				Debugger.writeerror(e);
				return null;
			}
		}else {
			Debugger.Sout("loadMovieList -> Failed to load \"" + List.getName() + "\"");
			return null;
		}
		return Filme;
	}
	
	private void showDetailsforMovie(Movie Film){
		Debugger.Sout("showDetailsForMovie -> Loading Movie Informations for Movie \'" + Film.getOPTitle() + "\'");
		String links = "";
		if (Film.getLink() != null){
			
			for (String temp:Film.getLink()){
				links += temp + "\n";
			}
			ta_downlink.setText(links);
			hl_op.setDisable(false);
		}
		//System.out.println("OPLINK: " + Film.getPostLink() + "\nLINK: " + links + "\nImageLink: " + Film.getImageLink() + "\nFileSize: " + Film.getFileSize() + "\nDuration: " + Film.getDuration());
		hl_op.setOnMouseClicked(event1 -> {
			if (Desktop.isDesktopSupported()) {
				try {
					Desktop.getDesktop().browse(new URI(Film.getPostLink()));
				} catch (IOException | URISyntaxException e) {
					e.printStackTrace();
				}
			}
		});
		try {
			if (Film.getImageLink() != "") {
				String link = Film.getImageLink().replace("\'","");
				BufferedImage bi = ImageIO.read(new URL(link));
				Image image = SwingFXUtils.toFXImage(bi, null);
				Debugger.Sout("showDetailsForMovie -> Trying to show the given Image with Path: " + link + " " + image.getHeight() + " x " + image.getWidth());

				while (image.isBackgroundLoading()){}
				if (image.isError() || (image.getHeight() == image.getWidth())){
					image_artwork.setImage(new Image(String.valueOf(ClassLoader.getSystemResource("images/nocover.png"))));
				}else
					image_artwork.setImage(image);
			} else {
				image_artwork.setImage(new Image(String.valueOf(ClassLoader.getSystemResource("images/nocover.png"))));
			}
		}catch (Exception e){
			//Debugger.writeerror(e);
			Debugger.Sout("showDetailsForMovie -> Failed to show the given Image with Path: " + Film.getImageLink());
			image_artwork.setImage(new Image(String.valueOf(ClassLoader.getSystemResource("images/nocover.png"))));
		}
	}

	/**
	 * Update table content.
	 *
	 * @param list the list
	 */
	public void updateTableContent(ArrayList<Movie> list){
		tableContent = FXCollections.observableArrayList();
		tableContent.addAll(list);
		maintable.setItems(tableContent);
	}
}
