package commandline;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * The type Movie.
 */
public class Movie implements Serializable {

    /**
     * The enum Quality.
     */
    public enum Quality {

        /**
         * Uhd quality.
         */
//Qualitys
        UHD(2160, 3840),
        /**
         * Fullhd quality.
         */
        FULLHD(1080, 1920),
        /**
         * Hdready quality.
         */
        HDREADY(720, 1280);

        //enum Description

        private final int Height;
        private final int Width;

        Quality(int height, int width){
            Height = height;
            Width = width;
        }

        /**
         * Gets height.
         *
         * @return the height
         */
        public int getHeight() {
		    return Height;
	    }

        /**
         * Gets width.
         *
         * @return the width
         */
        public int getWidth() {
		    return Width;
	    }
	    
	    @Override
	    public String toString(){
        	return Width + " x " + Height;
	    }
    }

    /**
     * The enum Codecs.
     */
    public enum Codecs{
        /**
         * H 265 codecs.
         */
        H265("H.265"),
        /**
         * H 264 codecs.
         */
        H264("H.264");

        Codecs(String s) {}
        
        
    }

    /**
     * The Name.
     */
    String Name;
    /**
     * The Drei d.
     */
    boolean DreiD;
    /**
     * The Op title.
     */
    String OPTitle;
    /**
     * The Dimension.
     */
    Quality Dimension;
    /**
     * The Link.
     */
    ArrayList<String> Link;
    /**
     * The Codec.
     */
    Codecs Codec;
    /**
     * The Year.
     */
    int Year;
    /**
     * The Post link.
     */
    String PostLink;
    /**
     * The Image link.
     */
    String ImageLink;
    /**
     * The Duration.
     */
    int Duration;   //In min.
    /**
     * The File size.
     */
    int FileSize;   //In MB

    /**
     * Instantiates a new Movie.
     *
     * @param optitle the optitle
     */
    public Movie(String optitle){
        this.OPTitle = optitle;
        this.PostLink = "";
        this.ImageLink = "";
        this.Name = "";
        this.Year = 0;
        this.DreiD = false;
        this.Dimension = Quality.HDREADY;
        this.Codec = Codecs.H264;
        this.Link = new ArrayList<>();
        this.FileSize = 0;
        this.Duration = 0;
    }

    /**
     * Instantiates a new Movie.
     *
     * @param name      the name
     * @param optitle   the optitle
     * @param year      the year
     * @param dreiD     the drei d
     * @param dimension the dimension
     * @param codec     the codec
     * @param link      the link
     * @param postLink  the post link
     * @param imageLink the image link
     * @param fileSize  the file size
     * @param duration  the duration
     */
    public Movie(String name, String optitle, int year, boolean dreiD, Quality dimension, Codecs codec, ArrayList<String> link, String postLink, String imageLink, int fileSize, int duration){
        this.Name = name;
        this.OPTitle = optitle;
        this.Year = year;
        this.DreiD = dreiD;
        this.Dimension = dimension;
        this.Codec = codec;
        this.Link = link;
        this.PostLink = postLink;
        this.ImageLink = imageLink;
        this.FileSize = fileSize;
        this.Duration = duration;
    }
    
    //GETTER AND SETTER

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
    	if (Name == null)return "";
        return Name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        Name = name;
    }

    /**
     * Is drei d boolean.
     *
     * @return the boolean
     */
    public boolean isDreiD() {
        return DreiD;
    }

    /**
     * Sets drei d.
     *
     * @param dreiD the drei d
     */
    public void setDreiD(boolean dreiD) {
        DreiD = dreiD;
    }

    /**
     * Gets op title.
     *
     * @return the op title
     */
    public String getOPTitle() {
        return OPTitle;
    }

    /**
     * Sets op title.
     *
     * @param OPTitle the op title
     */
    public void setOPTitle(String OPTitle) {
        this.OPTitle = OPTitle;
    }

    /**
     * Gets dimension.
     *
     * @return the dimension
     */
    public Quality getDimension() {
        return Dimension;
    }

    /**
     * Sets dimension.
     *
     * @param dimension the dimension
     */
    public void setDimension(Quality dimension) {
        Dimension = dimension;
    }

    /**
     * Gets link.
     *
     * @return the link
     */
    public ArrayList<String> getLink() {
        return Link;
    }

    /**
     * Sets link.
     *
     * @param link the link
     */
    public void setLink(ArrayList link) {
        Link = link;
    }

    /**
     * Gets codec.
     *
     * @return the codec
     */
    public Codecs getCodec() {
        return Codec;
    }

    /**
     * Sets codec.
     *
     * @param codec the codec
     */
    public void setCodec(Codecs codec) {
        Codec = codec;
    }

    /**
     * Gets year.
     *
     * @return the year
     */
    public int getYear() {
        return Year;
    }

    /**
     * Sets year.
     *
     * @param year the year
     */
    public void setYear(int year) {
        Year = year;
    }

    /**
     * Gets post link.
     *
     * @return the post link
     */
    public String getPostLink() {
    	if (PostLink==null)return "";
        return PostLink;
    }

    /**
     * Sets post link.
     *
     * @param postLink the post link
     */
    public void setPostLink(String postLink) {
        PostLink = postLink;
    }

    /**
     * Gets image link.
     *
     * @return the image link
     */
    public String getImageLink() {
        return ImageLink;
    }

    /**
     * Sets image link.
     *
     * @param imageLink the image link
     */
    public void setImageLink(String imageLink) {
        ImageLink = imageLink;
    }

    /**
     * Gets duration.
     *
     * @return the duration
     */
    public int getDuration() {
		return Duration;
	}

    /**
     * Sets duration.
     *
     * @param duration the duration
     */
    public void setDuration(int duration) {
		Duration = duration;
	}

    /**
     * Gets file size.
     *
     * @return the file size
     */
    public int getFileSize() {
		return FileSize;
	}

    /**
     * Sets file size.
     *
     * @param fileSize the file size
     */
    public void setFileSize(int fileSize) {
		FileSize = fileSize;
	}
}
