package com.idigital.asistenciasidigital;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.idigital.asistenciasidigital.api.IDigitalClient;
import com.idigital.asistenciasidigital.api.IDigitalService;
import com.idigital.asistenciasidigital.model.Login;
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
    private int loginAttempNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle(getResources().getString(R.string.autenticacion));
    }

    @OnClick(R.id.login_btn)
    public void onViewClicked() {

        if(loginAttempNumber >= 2){
            Toast.makeText(this, "Numero de intentos máximo alcanzado", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidUserInput()) {
            Toast.makeText(this, "Llena los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        showProgressDialog();
        new TestAndLoginAsyncTask().execute();
    }

    private void loginRequest() {

        loginAttempNumber++;
        progressView.setMessage("Autenticando...");

        IDigitalService service = IDigitalClient.getIDigitalService();
        //Call<LoginResponse> call = service.postLogin("kcervan@idteam.pe", "123456");
        Call<LoginResponse> call = service.postLogin(emailEtx.getText().toString(), passwordEtx.getText().toString());
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                Log.i(TAG, response.raw().toString());
                progressView.dismissDialog();
                if (response.isSuccessful()) {
                    LoginResponse loginResponse = response.body();
                    if (!loginResponse.getError()) {
                        saveLoginData(loginResponse);
                        navigateToRegisterActivity();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Autenticación incorrecta", Toast.LENGTH_SHORT).show();
                        loginAttempNumber++;
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                t.printStackTrace();
                progressView.dismissDialog();
            }
        });
    }

    private void navigateToRegisterActivity() {
        startActivity(new Intent(this, RegisterActivity.class));
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
                Toast.makeText(getApplicationContext(), "No estás conectado a internet", Toast.LENGTH_SHORT).show();
                return;
            }
            loginRequest();
        }
    }
}
