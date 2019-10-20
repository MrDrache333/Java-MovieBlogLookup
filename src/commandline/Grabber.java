package commandline;


import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import utils.Debugger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * The type Grabber.
 */
public class Grabber {

	/**
	 * The constant Zeit.
	 */
	public static FinishTime Zeit;

	/**
	 * The constant pForm.
	 */
	public static ProgressForm pForm;
	/**
	 * The Task.
	 */
	public static Task<Void> task;
	/**
	 * The Movies.
	 */
	public static ArrayList<Movie> Movies;

	/**
	 * The type Progress form.
	 */
	public static class ProgressForm {
		private final Stage dialogStage;
		
		private final ProgressBar pb = new ProgressBar();
		
		private final Label status = new Label();

		private final Label currentMovie = new Label();

		/**
		 * Instantiates a new Progress form.
		 */
		public ProgressForm(){
			dialogStage = new Stage();
			dialogStage.initStyle(StageStyle.UTILITY);
			dialogStage.setResizable(false);
			dialogStage.initModality(Modality.APPLICATION_MODAL);
			
			pb.setProgress(-1F);
			pb.setMinWidth(300);
			
			pb.progressProperty().addListener((observable, oldValue, newValue) -> {
				status.setText("Noch " + FinishTime.format(Zeit.getEstimatedNeededTime()));
				currentMovie.setText("\"" + Movies.get(Movies.size()-1).getName() + "\"");
			});

			status.setText("Suche Links...");
			status.setTextFill(Color.BLACK);
			currentMovie.setTextFill(Color.PURPLE);
			final BorderPane hb = new BorderPane();
			hb.setStyle("-fx-border-color: black");
			hb.setBottom(currentMovie);
			hb.setCenter(status);
			hb.setTop(pb);
			hb.setAlignment(pb, Pos.CENTER);
			hb.setAlignment(status,Pos.CENTER);
			hb.setAlignment(currentMovie, Pos.CENTER);
			
			dialogStage.setTitle("Datenbankupdate");
			Scene scene = new Scene(hb);
			dialogStage.setScene(scene);
		}

		/**
		 * Activate progress bar.
		 *
		 * @param task the task
		 */
		public void activateProgressBar(final Task<?> task)  {
			pb.progressProperty().bind(task.progressProperty());
			
			dialogStage.show();
		}

		/**
		 * Gets dialog stage.
		 *
		 * @return the dialog stage
		 */
		public Stage getDialogStage() {
			return dialogStage;
		}
	}

	/**
	 * Update movies.
	 *
	 * @param mainurls        the urls
	 * @param paths			the Category Path
	 * @param maxThreads the max threads
	 */
	public static void updateMovies(String[] mainurls,String[] paths, int maxThreads) {
		int totalSites = 0;
		int Sites[] = {0,0};
		for (int i = 0; i < mainurls.length;i++){
			try {
				if (paths[i] == null)throw new MalformedURLException("Path is null");
				URL url = new URL(mainurls[i] + paths[i]);
				Sites[i] = getNumberOfSites(url);
				Debugger.Sout("updatesMovies -> Found nearly " + Sites[i] * 10 + " Movies on \"" + mainurls[i] + paths[i] + "\"");
			}catch (MalformedURLException e){
				Sites[i] = 0;
			}
		}
		for (int sites : Sites)totalSites += sites;
	    Zeit = new FinishTime(totalSites,maxThreads*10);
	    Movies = new ArrayList<>();
	    ArrayList<Thread> Threads = new ArrayList<>();
	
	    pForm = new ProgressForm();

		int finalSites = totalSites;
		task = new Task<Void>() {
			@Override
			protected Void call() {
				for (int i = 0; i< mainurls.length; i++) {
					for (int Page = 1; Page <= Sites[i]; Page++) {
						int finalPage = Page;
						int finalI = i;
						Thread tempThread = new Thread(() -> {
							//Debugger.Sout("Crawling informations on Site " + Debugger.equalLenghts(finalPage + "", finalSites + "","0") + " of " + finalSites + " Finish in " + Zeit.format(Zeit.getEstimatedNeededTime()) + " at " + new SimpleDateFormat("HH:mm:ss").format(Zeit.getEstimatedFinishTime()));
							URL Link = null;
							try {
								Link = new URL(mainurls[finalI] + paths[finalI] + "/page/" + finalPage + "/");
							} catch (MalformedURLException e) {
								e.printStackTrace();
							}
							ArrayList<Movie> Filme = getMovieInformationfromWebsite(Link);
							if (Filme != null) {
								Movies.addAll(Filme);
							}
						});
						Threads.add(tempThread);
					}
				}
				int ready = 0;
				int lastready = 0;
				int alive;
				Zeit.start();
				ArrayList<Thread> runningThreads = new ArrayList<>();
				for (int i = 0; i < (maxThreads > finalSites ? finalSites :maxThreads); i++){
					runningThreads.add(Threads.get(0));
					Threads.remove(0);
				}
				for (Thread temp:runningThreads){
					temp.start();
				}
				do{
					alive = 0;
					for (int i = 0; i < runningThreads.size();i++){
						if (runningThreads.get(i).isAlive()){
							alive++;
						}else{
							runningThreads.remove(i);
							ready++;
							updateProgress(ready, finalSites);
							Zeit.addCheckPoint();
						}
						
					}
					if (lastready != ready){
						Debugger.Sout("Crawling Information " + Debugger.equalLenghts(ready, finalSites, " ") + " of " + finalSites + "\tThreads running:" + Debugger.equalLenghts(alive,maxThreads," ") + "\tMovies found: " + Debugger.equalLenghts(Movies.size(),(finalSites *10)," ") + "\tFinish in " + FinishTime.format(Zeit.getEstimatedNeededTime()) + " at " + new SimpleDateFormat("HH:mm:ss").format(Zeit.getEstimatedFinishTime()));
					}
					int index = runningThreads.size();
					for (int i = alive; i <= maxThreads;i++){
						if (Threads.size() > 0){
							runningThreads.add(Threads.get(0));
							Threads.remove(0);
							runningThreads.get(index).start();
							index++;
						}else
							break;
					}
					
					lastready = ready;
				}while(ready != finalSites);
				updateProgress(finalSites, finalSites);
				return null;
			}
		};
		
    	pForm.activateProgressBar(task);
	    pForm.getDialogStage().show();
	    
	    Thread thread = new Thread(task);
	    thread.start();
    }

	/**
	 * Get number of sites int.
	 *
	 * @param url the url
	 * @return the int
	 */
	public static int getNumberOfSites(URL url){
	    int Sites = 1;
	    ArrayList<String> TempSite = getWebsiteContent(url);
	    if (TempSite != null) {
		    for (String line:TempSite){
			    if (line.contains("Seite 1 von")){
				    line = line.substring(line.indexOf("von") + 4);
				    line = line.substring(0, line.indexOf("<"));
				    line = line.replace(".","");
				    Sites = Integer.parseInt(line);
			    }else if (line.contains("Page 1 of")){
					line = line.substring(line.indexOf("of") + 3);
					line = line.substring(0, line.indexOf("<"));
					line = line.replace(".","");
					Sites = Integer.parseInt(line);
				}
		    }
	    }
	    return Sites;
    }
    
    private static ArrayList<Movie> getMovieInformationfromWebsite(URL Link) {
	    ArrayList<Movie> output = new ArrayList<>();
	    ArrayList<String> Website = getWebsiteContent(Link);
	    if (Website != null)
		    for (int i = 0; i < Website.size(); i++) {
			    if (Website.get(i).equals("<div class=\"beitrag2\">")) {
				
				    i += 5;
				    String optitle = getValueforKey(Website.get(i), "title").substring(18).replace('.', ' ');
				    String PostLink = getValueforKey(Website.get(i), "href");
				    Movie Film = new Movie(optitle);
				    Film.setPostLink(PostLink);

				    //Get Information out of the Title
				    ArrayList<String> Attributes = new ArrayList<>(Arrays.asList(optitle.split(Pattern.quote(" "))));
				    for (String temp : Attributes) {
					    if (temp.contains("1080p")) {
						    Film.setDimension(Movie.Quality.FULLHD);
					    } else if (temp.contains("4K") || temp.contains("2160p") || temp.contains("UHD")) {
						    Film.setDimension(Movie.Quality.UHD);
					    }
					
					    if (temp.contains("x265") || temp.contains("H265") || temp.contains("HEVC")) {
						    Film.setCodec(Movie.Codecs.H265);
					    }
					    if (temp.contains("3D")) {
						    Film.setDreiD(true);
					    }
					    if (Film.getYear() == 0)
						    if (temp.length() == 4) {
							    try {
								    int Year = Integer.parseInt(temp);
								    int cur = Integer.parseInt(new SimpleDateFormat("YYYY").format(new Date().getTime()));
								    if (Year <= cur && Year >= 1900)Film.setYear(Year);
							    } catch (NumberFormatException ignored) {
							    }
						    }
				    }

				    //Extract the real Title by deleting unwanted Keywords
					String filterExact[] = {"LD","TS",Film.getYear() + "","MD","Proper","DC","Complete","Collection","Dual","HC"};

				    String filterContains[] = {"AC3","Dubbed","German","x264","x265","WEBRip","4K","UHD","720","2160","HD","1080","BDRip","DVD","Unrated","Theatrical","Uncut"};
				    String Title = "";

				    AttributeLoop:
				    for (String attribute:Attributes){

				    	for (String exact:filterExact){
				    		if (exact.toLowerCase().equals(attribute.toLowerCase()))break AttributeLoop;
						}

				    	for (String contains:filterContains){
							if (attribute.equals(contains))break AttributeLoop;
				    		if (attribute.toLowerCase().contains(contains.toLowerCase()))break AttributeLoop;
						}
				    	Title += (Title.equals("") ? "":" ") + attribute;
					}
					Film.setName(Title);
				    
					//Parse the WebSite
				    while (Website.size() > i && !Website.get(i).contains("<div class=\"beitrag")) {
				    	String Line = Website.get(i).replace(" ","");
					    String LineWithSpaces = Website.get(i);
					    //Get Downloadlinks
					    if (Line.toLowerCase().contains("zippyshare")) {
					    	//Check the Downloadlink
						    String downlink = getValueforKey(Line, "href");
							try {
								ArrayList<String> DownSite;
								if ((DownSite = getWebsiteContent(new URL(downlink))).size() != 0){
									boolean add = true;
									for (String dsline:DownSite){
										if (dsline.contains("<strong>not</strong>"))add = false;
									}
									if (downlink.contains("noref.co"))add = false;
									if (add) {
										ArrayList<String> links = Film.getLink();
										links.add(downlink);
										Film.setLink(links);
									}
								}
							} catch (Exception ignored) {}
						}
					    if (Line.contains("<imgsrc") && Film.getImageLink().equals("")) {
						    String imageLink = getValueforKey(LineWithSpaces, "src");
						    if (!getValueforKey(Line,"height").equals("") && !getValueforKey(Line,"width").equals(""))
						        Film.setImageLink(imageLink);
					    }
					    if (Line.contains("<strong>Dauer:</strong>")) {
						    String duration = Line;
						    String pointer = "<strong>Dauer:</strong>";
						    try {
							    duration = duration.substring(duration.indexOf(pointer));
							    duration = duration.substring(pointer.length());
							    duration = duration.toLowerCase();
							    
							    if (duration.contains("min.")) duration = duration.replace("min.","");
							    if (duration.contains("min")) duration = duration.replace("min","");
							    if (duration.contains("<")) duration = duration.substring(0, duration.indexOf("<"));
							
							    char unit = duration.contains("h") ? 'h':'m';
							    
							    if (duration.contains("profolgeca.")) duration = duration.substring(duration.indexOf("profolgeca.") + "profolgeca.".length());
							    if (duration.contains("profolge")) duration = duration.substring(duration.indexOf("profolge") + "profolge".length());
							    if (duration.contains("proep")) duration = duration.replace("proep", "");
							    if (duration.contains("proepisode")) duration = duration.replace("proepisode", "");
							    if (duration.contains("folge")) duration = duration.replace("folge", "");
							    if (duration.contains("uten")) duration = duration.replace("uten", "");
							    if (duration.contains("ep.")) duration = duration.replace("ep.", "");
							    if (duration.contains("ep")) duration = duration.replace("ep", "");
							    
							    if (duration.contains("jeca")) duration = duration.substring(duration.indexOf("jeca") + "jeca".length());
							    if (duration.contains("clip")) duration = duration.substring(duration.indexOf("clip") + "clip".length());
							    if (duration.contains("ca.")) duration = duration.substring(duration.indexOf("ca.") + "ca.".length());
							    if (duration.contains("ca")) duration = duration.substring(duration.indexOf("ca") + "ca".length());
							    if (duration.contains("je")) duration = duration.substring(duration.indexOf("je") + "je".length());
							    if (duration.contains("~")) duration = duration.substring(duration.indexOf("~") + "~".length());
							    if (duration.contains("std.")){
								    duration = duration.replace("std.","");
								    unit = 'h';
							    }
							    if (duration.contains("std")){
								    duration = duration.replace("std","");
								    unit = 'h';
							    }
							    
							    if (duration.contains("&#215;")) duration = duration.replace("&#215;", "x");
							    
							    double dur = 0;
							    if (duration.length() >= 3 && duration.contains("h")){
								    if (duration.contains("|"))duration = duration.replace("|","");
								    int h = Integer.parseInt(duration.substring(0,duration.indexOf("h")));
							        duration = duration.substring(duration.indexOf("h")+1);
							        duration = duration.length() == 1 ? duration + "0":duration;
							        int m = Integer.parseInt(duration);
							        dur = h+m;
							    }else if (duration.length() >= 3 && duration.contains(":")){
								    if (duration.contains("|"))duration = duration.replace("|","");
							    	if (unit == 'm'){
							    		dur = Integer.parseInt(duration.substring(0, duration.indexOf(":")));
								    }else {
									    int h = Integer.parseInt(duration.substring(0, duration.indexOf(":")));
									    duration = duration.substring(duration.indexOf(":") + 1);
									    duration = duration.length() == 1 ? duration + "0" : duration;
									    int m = Integer.parseInt(duration.substring(0, duration.indexOf(":")));
									    dur = h + m;
								    }
							    }else if (duration.length() >= 3 && duration.contains("+")){
							    	dur = getSumOfNumbersInString(duration,'+');
							    }else if (duration.length() >= 3 && duration.contains("/")){
								    dur = getSumOfNumbersInString(duration,'/');
							    }else if (duration.length() >= 3 && duration.contains("x")){
								    dur = getProductOfNumbersInString(duration,'x');
							    }else if (duration.length() >= 3 && duration.contains("×")){
								    dur = getProductOfNumbersInString(duration,'×');
							    }else if (duration.length() >= 3 && duration.contains("*")){
								    dur = getProductOfNumbersInString(duration,'*');
							    }else if (duration.length() >= 3 && duration.contains(".")){
								    int h = Integer.parseInt(duration.substring(0,duration.indexOf(".")));
								    duration = duration.substring(duration.indexOf(".")+1);
								    duration = duration.length() == 1 ? duration + "0":duration;
								    int m = Integer.parseInt(duration.substring(0,duration.indexOf(".")));
								    dur = h+m;
							    }else if (duration.length() >= 3 && duration.contains("|") && !duration.contains(":")){
								    dur = getSumOfNumbersInString(duration,'|');
							    }
							    if (duration.contains("|"))duration = duration.replace("|","");
							    if (dur == 0)dur = Integer.parseInt(duration);
							    if (unit == 'h')dur*=60;
							    Film.setDuration((int)(dur));
						    } catch (Exception e) {
							    if (duration.length() > 0)Debugger.Sout("Failed parsing Duration: " + duration + " for Movie from \'" + Film.getPostLink() + "\'");
						    }
					    }
					    
					    boolean groesse = false;
					    String variants[] = {"<strong>Größe:</strong>","<strong>Größe</strong>","<strong>Gr&ouml;&szlig;e</strong>","<strong>Gr&ouml;&szlig;e:</strong>","<strong>Groesse:</strong>","<strong>Groesse</strong>"};
					    int variant = 0;
					    for (String var:variants){
					    	if (Line.contains(var)){
					    		groesse = true;
					    		break;
						    }else
						    	variant++;
					    }
					    if (groesse) {
						    String pointer = variants[variant];
						    String size = Line;
						    try {
							    size = size.substring(size.indexOf(pointer) + pointer.length());
							    if (size.contains("<")) size = size.substring(0, size.indexOf("<"));
							    size = size.toLowerCase();
							
							    double fs = 0;
							    char unit = 'm';
							
							    if (size.contains("jeca.")) size = size.replace("jeca.", "");
							    if (size.contains("jeca")) size = size.replace("jeca", "");
							    if (size.contains("insg.")) size = size.replace("insg.", "");
							    if (size.contains("insg")) size = size.replace("insg", "");
							    if (size.contains("imdb")) size = size.replace("imdb", "");
							    if (size.contains("jefolge")) size = size.replace("jefolge", "");
							    if (size.contains("profolge")) size = size.replace("profolge", "");
							    if (size.contains("proep")) size = size.replace("proep", "");
							    if (size.contains("proepisode")) size = size.replace("proepisode", "");
							    if (size.contains("isode")) size = size.replace("isode", "");
							    if (size.contains("ca.")) size = size.replace("ca.", "");
							    if (size.contains("ca")) size = size.replace("ca", "");
							    if (size.contains("&#215;")) size = size.replace("&#215;", "x");
							    if (size.contains("je")) size = size.replace("je", "");
							    if (size.contains("ep.")) size = size.replace("ep.", "");
							    if (size.contains("ep")) size = size.replace("ep", "");
							    if (size.contains("~")) size = size.replace("~", "");
							
							    if (size.contains("mb")){
							    	size = size.replace("mb", "");
							    }else
							    	if (size.contains("gb")) {
								    unit = 'g';
								    size = size.replace("gb", "");
							    }
							    
							    if (size.contains(",")) {
								    size = size.replace(",", ".");
							    }
							    
							    if (size.length() >= 3 && size.contains("/") && !size.contains(":")) {
								    fs = getSumOfNumbersInString(size, '/');
							    } else if (size.length() >= 3 && size.contains("+") && !size.contains(":")) {
								    fs = getSumOfNumbersInString(size, '+');
							    } else if (size.length() >= 3 && size.contains("x") && !size.contains(":")) {
								    fs = getProductOfNumbersInString(size, 'x');
							    }else if (size.length() >= 3 && size.contains("×") && !size.contains(":")){
								    fs = getProductOfNumbersInString(size,'×');
							    }else if (size.length() >= 3 && size.contains("*") && !size.contains(":")){
								    fs = getProductOfNumbersInString(size,'*');
							    } else if (size.length() >= 3 && size.contains("|") && !size.contains(":")) {
								    fs = getSumOfNumbersInString(size, '|');
							    }
							    
							    if (size.contains("|"))size = size.replace("|","");
											
							
							    if (size.contains(":")) size = size.replace(":", "");
							
							    if (fs == 0) fs = Double.parseDouble(size);
							    if (unit == 'g')fs*=1000;
							    Film.setFileSize((int) fs);
						    } catch (Exception e) {
							    if (Film.getFileSize() == 0 && size.length() > 0){
							    	Debugger.Sout("Exception Parsing Size: "+ size + " for Movie from \'" + Film.getPostLink() + "\'");
							    	Debugger.writeerror(e);
							    }
							
						    }
						    if (Film.getFileSize() == 0 && size.length() > 0)Debugger.Sout("Invalid Format Parsing Size: "+ size + " for Movie from \'" + Film.getPostLink() + "\' ?");
					    }
					    i++;
				    }
				    if (Film.ImageLink.equals("")){
				    	try{
				    		ArrayList<String> PostWebsite = getWebsiteContent(new URL(Film.getPostLink()));
				    		for (int line = 0; line < PostWebsite.size();line++){
				    			if (PostWebsite.get(line).contains("<div class=\"eintrag")){
				    				while(Film.getImageLink().equals("") && line < PostWebsite.size()){
								        if (PostWebsite.get(i).contains("<img") && !Film.getImageLink().equals("")){
								            String temp = getValueforKey(PostWebsite.get(line),"src");
								            if (temp.length() > 10){
									            if (!getValueforKey(PostWebsite.get(i),"height").equals("") && !getValueforKey(PostWebsite.get(i),"width").equals(""))
								            	Film.setImageLink(temp);
								            }
								        }
								        line++;
				    				}
							    }
							    if (!Film.getImageLink().equals(""))break;
						    }
					    }catch (Exception e){
				    		Debugger.Sout("Failed getting new ImageLink");
				    		Debugger.writeerror(e);
					    }
				    }
				    if (!Film.getLink().isEmpty()) output.add(Film);
			    }
			
			
		    }
	    return output;
    }
    
    private static double getSumOfNumbersInString(String input,char divider){
		double sum = 0;
		if (input.contains(" "))input = input.replace(" ","");
		if (divider != '|')if (input.contains("|"))input = input.replace("|","");
	    if (input.charAt(input.length()-1) == '|')input = input.substring(0,input.length()-2);
		if (input.contains(divider + "")) {
			while (input.contains(divider + "") && input.length()>1) {
				sum += Double.parseDouble(input.substring(0, input.indexOf(divider)));
				input = input.substring(input.indexOf(divider)+1);
			}
			sum += Double.parseDouble(input);
		}
		return sum;
    }
	
	private static double getProductOfNumbersInString(String input,char divider){
		double product = 0;
		if (input.contains(" "))input = input.replace(" ","");
		if (divider != '|')if (input.contains("|"))input = input.replace("|","");
		if (input.charAt(input.length()-1) == '|')input = input.substring(0,input.length()-2);
		if (input.contains(divider + "")) {
			while (input.contains(divider + "") && input.length()>1) {
				if (product == 0) {
					product = Double.parseDouble(input.substring(0, input.indexOf(divider)));
				}else
					product *= Double.parseDouble(input.substring(0, input.indexOf(divider)));
				input = input.substring(input.indexOf(divider)+1);
			}
			product *= Double.parseDouble(input);
		}
		return product;
	}

	/**
	 * Gets website content.
	 *
	 * @param url the url
	 * @return the website content
	 */
//Slim Way getting the Content of any normal Website
    public static ArrayList<String> getWebsiteContent(URL url) {
	    int maxattempts = 3;
	    for (int i = 1; i <= maxattempts;i++) {
		    try {
			    URLConnection conn = url.openConnection();
			    conn.setConnectTimeout(10000);
			    conn.setReadTimeout(5000);
			    conn.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; U; Linux x86_64; en-GB; rv:1.8.1.6) Gecko/20070723 Iceweasel/2.0.0.6 (Debian-2.0.0.6-0etch1)");
			    conn.addRequestProperty("Accept","text/html");
			    BufferedReader in = new BufferedReader(
					    new InputStreamReader(conn.getInputStream())
			    );

			    ArrayList<String> Website = new ArrayList<>();
			
			    String line;
			    while ((line = in.readLine()) != null) {
				    Website.add(line);
			    }
			    if (Website.size() == 0)throw new NullPointerException("Website has no Content!");
			    return Website;
		    } catch (Exception e) {
			    Debugger.Sout("Failed to crawl Website Informations. Attempt " + i + " of " + maxattempts);
			    //e.printStackTrace();
		    }
	    }
	    Debugger.writeerror(new Exception("Failed to crawl Website Informations of Site \"" + url.getProtocol() + "://" + url.getHost() + url.getPath() + " " + maxattempts + " Times! Probably down?"));
	
	    return null;
    }

	/**
	 * Get valuefor key string.
	 *
	 * @param input the input
	 * @param Key   the key
	 * @return the string
	 */
//Simple function to get the Value for a Key in an xml like Line
    public static String getValueforKey(String input, String Key){
        String Value = "";
        try {
	        if (input.contains(Key + "=\"")) {
		        Value = input.substring(input.indexOf(Key + "=\"") + (Key + "=\"").length());
		        Value = Value.substring(0, Value.indexOf("\""));
	        } else {
		        if (input.contains(Key + "=")) {
			        Value = input.substring(input.indexOf(Key + "=") + (Key + "=").length());
			        if (Value.contains(",")) {
				        if (Value.contains(" ")) {
					        if (Value.indexOf(" ") < Value.indexOf(",")) {
						        Value = Value.substring(0, Value.indexOf(" "));
					        } else
						        Value = Value.substring(0, Value.indexOf(","));
				        } else
					        Value = Value.substring(0, Value.indexOf(","));
			        } else {
				        if (Value.contains(" ")) {
					        Value = Value.substring(0, Value.indexOf(" "));
				        }
			        }
			
		        }
	        }
	        if (Value.contains("\"")) Value.replace("\"", "");
	        if (Value.contains("'")) Value.replace("'", "");
	        if (Value.contains(",")) Value.replace(",", "");
        }catch (Exception e){
        	Debugger.writeerror(e);
        	return "";
        }
        return Value;
    }
	
}
