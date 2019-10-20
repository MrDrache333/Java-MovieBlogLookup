/*
 *   $class.name
 *   MovieBlogLookup
 *
 *   Created by Keno Oelrichs Garcia on $today.date
 *   Copyright (c) 2018 Keno Oelrichs Garcia. All rights reserved.
 */

package utils;

import javafx.scene.control.Alert;
import javafx.stage.Modality;

/**
 * Created by keno on 12.06.17.
 */
public class AlertDialog {

    public static void showAlert(Alert.AlertType type, String message, String title){
        Alert alert = new Alert(type, "");
        alert.setResizable(false);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.getDialogPane().setContentText(message);
        alert.getDialogPane().setHeaderText(title);
        alert.show();
	    try {
		    Thread.sleep(2000);
	    } catch (InterruptedException e) {
		    e.printStackTrace();
	    }
    }
}
