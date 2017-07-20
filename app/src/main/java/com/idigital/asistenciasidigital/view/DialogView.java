package com.idigital.asistenciasidigital.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.idigital.asistenciasidigital.R;
import com.idigital.asistenciasidigital.util.Constants;

/**
 * Created by USUARIO on 31/05/2017.
 */

public class DialogView {

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

    public static void showDialog(final Context context, String message, int type, final Activity listener) {

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_dialog);
        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
        TextView messageTxv = (TextView) dialog.findViewById(R.id.message_txv);
        ImageView imageView = (ImageView) dialog.findViewById(R.id.dialog_icon_igv);
        TextView textView = (TextView) dialog.findViewById(R.id.dialog_title);
        if (Constants.SUCCESS_DIALOG == type) {

            imageView.setImageResource(R.drawable.ic_icono_exito);
            textView.setText("Correcto");
        } else if (Constants.ALERT_DIALOG == type) {

            imageView.setImageResource(R.drawable.ic_icono_alerta);
            textView.setText("Alerta");
        } else {
            throw new RuntimeException("Invalid type dialog");
        }

        messageTxv.setText(message);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (listener != null)
                    listener.finish();
            }
        });

        dialog.show();
    }
}
