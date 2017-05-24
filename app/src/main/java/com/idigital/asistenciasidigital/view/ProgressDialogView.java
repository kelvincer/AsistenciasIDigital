package com.idigital.asistenciasidigital.view;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by USUARIO on 10/04/2017.
 */

public class ProgressDialogView {

    private ProgressDialog progressDialog;

    public ProgressDialogView(Context context) {

        progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Cargando...");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    public void setMessage(String message) {

        progressDialog.setMessage(message);
    }

    public void showProgressDialog() {

        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    public void dismissDialog() {

        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
}
