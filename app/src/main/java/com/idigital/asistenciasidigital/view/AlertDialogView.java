package com.idigital.asistenciasidigital.view;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by USUARIO on 31/05/2017.
 */

public class AlertDialogView {

    public static void showInternetAlertDialog(Activity activity, String message) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle("Alerta");
        alertDialog.setMessage(message);

        alertDialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();
    }
}
