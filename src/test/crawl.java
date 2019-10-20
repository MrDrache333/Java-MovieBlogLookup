/*
 *   $class.name
 *   MovieBlogLookup
 *
 *   Created by Keno Oelrichs Garcia on $today.date
 *   Copyright (c) 2018 Keno Oelrichs Garcia. All rights reserved.
 */

package test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class crawl {

    public static void main(String[] args) throws MalformedURLException {
        ArrayList<String> site = commandline.Grabber.getWebsiteContent(new URL("http://filecrypt.cc/Container/90BAEC76DA.html"));
        for (String line:site){
            System.out.println(line);
        }

    }


}
