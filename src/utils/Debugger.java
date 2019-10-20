package utils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Project: Soundboard
 * Package: editor
 * Created by keno on 02.06.17.
 */
public class Debugger {

    private static File detaillog = new File("temp/detaillog.log");
    private static File logfile = new File("temp/log.log");
    
    private static String lastoutput;

    //Gibt Informationen in der Console aus und schreibt diese zusaetzlich in einen Detaillierten Log -> Fuer Offline Debugging
    public static boolean Sout(String output){
        System.out.println(output);
        lastoutput = output;
        boolean success = true;
        try {
            FileWriter fw = new FileWriter(detaillog , true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(new SimpleDateFormat("YYYY-MM-dd_HH:mm:ss - ").format(new Date().getTime()) + output + "\n");
            bw.close();
        }catch(Exception ignored){
            success = false;
        }
        return success;
    }
    
    public static void deleteLastConsoleOutput(){
        for (int i = 0; i < lastoutput.length();i++){
            System.out.print('\b');
        }
    }

    static boolean printDebug(String type, String message){
        return Sout("DEBUG -> " + type + ": " + message);
    }

    //Schreibt eine Logdatei, welche Informationen zu evtl. auftretenden Fehlern enthaelt
    private static boolean writetolog(String output){
        Sout(output);   //Consolenausgabe
        boolean success = true;
        //Versuchen die Informationen in die Logdatei zu schreiben + Datum und Uhrzeit
        try {
            FileWriter fw = new FileWriter(logfile, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(new SimpleDateFormat("YYYY-MM-dd_HH:mm:ss - ").format(new Date().getTime()) + output + "\n");
            bw.close();
        }catch(Exception ignored){
            success = false;
        }
        return success;
    }

    //Fehlermeldungen in Log schreiben und ggf. Report Senden
    public static void writeerror(Exception e){
        //Entstandene Fehler abfangen und in LogDatei schreiben
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        writetolog(sw.toString());

    }
    
    public static String equalLenghts(String input, String output, String filler){
        while(input.length() < output.length()){
        	input = filler +input;
        }
        return input;
    }
    public static String equalLenghts(int input, int output, String filler){
        return equalLenghts(input +"",output +"",filler);
    }
}
