package com.idigital.asistenciasidigital.register.ui;

/**
 * Created by USUARIO on 24/05/2017.
 */

public interface RegisterView {

    void showProgressDialog();
    void hideProgressDialog();
    void setProgressMessage(String message);
    void showAlert(String message);
    void updateButton(String message);
    void updateList(String message);
}
