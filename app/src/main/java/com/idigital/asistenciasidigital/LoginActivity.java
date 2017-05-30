package com.idigital.asistenciasidigital;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.idigital.asistenciasidigital.api.IDigitalClient;
import com.idigital.asistenciasidigital.api.IDigitalService;
import com.idigital.asistenciasidigital.model.Login;
import com.idigital.asistenciasidigital.register.ui.*;
import com.idigital.asistenciasidigital.response.LoginResponse;
import com.idigital.asistenciasidigital.util.Constants;
import com.idigital.asistenciasidigital.view.ProgressDialogView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    @BindView(R.id.email_etx)
    EditText emailEtx;
    @BindView(R.id.password_etx)
    EditText passwordEtx;
    @BindView(R.id.login_btn)
    Button loginBtn;

    ProgressDialogView progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle(R.string.authentication_title);

        PreferenceManager preferenceManager = new PreferenceManager(this);
        boolean versionUpdated = preferenceManager.getBoolean(Constants.VERSION_UPDATE, true);
        if (!versionUpdated) {
            String message = getIntent().getStringExtra(Constants.FETCH_VERSION_MESSAGE);
            showUpdateAppVersionDialog(message);
        }
    }

    @OnClick(R.id.login_btn)
    public void onViewClicked() {

        if (!isValidUserInput()) {
            Toast.makeText(this, "Completa los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        showProgressDialog();
        new TestAndLoginAsyncTask().execute();
    }

    private void loginRequest() {

        progressView.setMessage("Autenticando...");

        IDigitalService service = IDigitalClient.getIDigitalService();
        //Call<LoginResponse> call = service.postLogin("kcervan@idteam.pe", "123456");
        Call<LoginResponse> call = service.postLogin(emailEtx.getText().toString(),
                passwordEtx.getText().toString());
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                Log.i(TAG, response.raw().toString());
                progressView.dismissDialog();
                if (response.isSuccessful()) {
                    LoginResponse loginResponse = response.body();
                    if (loginResponse.getCode() == 5) {
                        saveLoginData(loginResponse);
                        navigateToRegisterActivity();
                        finish();
                    } else {
                        //Toast.makeText(getApplicationContext(), "Autenticación incorrecta", Toast.LENGTH_SHORT).show();
                        showAlertDialog(loginResponse.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                t.printStackTrace();
                progressView.dismissDialog();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.service_failed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToRegisterActivity() {

        Intent intent = new Intent(this, com.idigital.asistenciasidigital.register.ui.RegisterActivity.class);
        intent.putExtra(Constants.PASS_FOR_LOGIN, true);
        startActivity(intent);
    }

    private void saveLoginData(LoginResponse response) {

        Login login = response.getData();
        PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext());
        preferenceManager.putString(Constants.USER_PASSWORD, passwordEtx.getText().toString());
        preferenceManager.putString(Constants.USER_EMAIL, login.getEmail());
        preferenceManager.putString(Constants.USER_ID, login.getIdUser());
        preferenceManager.putString(Constants.USER_NAME, login.getName());
        preferenceManager.putString(Constants.USER_LAST_NAME, login.getLastname());

        //save login
        preferenceManager.putBoolean(Constants.LOGGED_IN, true);
    }

    private boolean isValidUserInput() {

        if (emailEtx.getText().toString().isEmpty())
            return false;

        if (passwordEtx.getText().toString().isEmpty())
            return false;

        return true;
    }

    public void showProgressDialog() {

        progressView = new ProgressDialogView(this);
        progressView.setMessage("Conectando...");
        progressView.showProgressDialog();
    }

    private class TestAndLoginAsyncTask extends TestConnectionAsyncTask {

        @Override
        protected void onPostExecute(Boolean result) {
            // Activity 1 GUI stuff
            super.onPostExecute(result);
            if (!result) {
                progressView.dismissDialog();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                return;
            }
            loginRequest();
        }
    }

    public void showAlertDialog(String message) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Alerta");
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void showUpdateAppVersionDialog(String message) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Alerta");
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(R.string.alert_cancelar, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.setNegativeButton(R.string.alert_aceptar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertDialog.show();
    }
}
