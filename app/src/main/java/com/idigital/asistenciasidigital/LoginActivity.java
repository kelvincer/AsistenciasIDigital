package com.idigital.asistenciasidigital;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.idigital.asistenciasidigital.api.IDigitalClient;
import com.idigital.asistenciasidigital.api.IDigitalService;
import com.idigital.asistenciasidigital.model.Login;
import com.idigital.asistenciasidigital.response.LoginResponse;
import com.idigital.asistenciasidigital.util.ConnectionUtil;
import com.idigital.asistenciasidigital.util.Constants;
import com.idigital.asistenciasidigital.view.ProgressDialogView;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = LoginActivity.class.getSimpleName();
    @BindView(R.id.login_btn)
    Button button;
    @BindView(R.id.etx_email)
    EditText etxEmail;
    @BindView(R.id.etx_password)
    EditText etxPassword;

    ProgressDialogView progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.login_btn) {

            /*if (!ConnectionUtil.haveNetworkConnection(this)) {
                Toast.makeText(getApplicationContext(), "No estás conectado a internet", Toast.LENGTH_SHORT).show();
                return;
            }*/
            if (!ConnectionUtil.isOnline()) {
                Toast.makeText(getApplicationContext(), "No estás conectado a internet", Toast.LENGTH_SHORT).show();
                return;
            }

            loginRequest();
        }
    }

    private void loginRequest() {

        if (!isValidUserInput()) {
            Toast.makeText(this, "Llena los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        showProgressDialog();

        IDigitalService service = IDigitalClient.getClubService();
        //Call<LoginResponse> call = service.postLogin("kcervan@idteam.pe", "123456");
        Call<LoginResponse> call = service.postLogin(etxEmail.getText().toString(), etxPassword.getText().toString());
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                Log.i(TAG, response.raw().toString());
                progressView.dismissDialog();
                if (response.isSuccessful()) {
                    LoginResponse loginResponse = response.body();
                    if (!loginResponse.getError()) {
                        saveLoginData(loginResponse);
                        gotoRegisterActivity();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Autenticación incorrecta", Toast.LENGTH_SHORT).show();
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

    private void gotoRegisterActivity() {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    private void saveLoginData(LoginResponse response) {

        Login login = response.getData();
        PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext());
        preferenceManager.putString(Constants.USER_ID, login.getIdUser());
        preferenceManager.putString(Constants.USER_NAME, login.getName());
        preferenceManager.putString(Constants.USER_LAST_NAME, login.getLastname());
    }

    private boolean isValidUserInput() {

        if (etxEmail.getText().toString().isEmpty())
            return false;

        if (etxPassword.getText().toString().isEmpty())
            return false;

        return true;
    }

    public void showProgressDialog() {

        progressView = new ProgressDialogView(this);
        progressView.setMessage("Autenticando...");
        progressView.showProgressDialog();
    }
}
