package com.idigital.asistenciasidigital;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.idigital.asistenciasidigital.api.IDigitalClient;
import com.idigital.asistenciasidigital.api.IDigitalService;
import com.idigital.asistenciasidigital.response.LoginResponse;
import com.idigital.asistenciasidigital.util.ConnectionUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = LoginActivity.class.getSimpleName();
    @BindView(R.id.login_btn)
    Button button;

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

            if (!ConnectionUtil.isConnected(this)) {
                Toast.makeText(getApplicationContext(), "No estás conectado a internet", Toast.LENGTH_SHORT).show();
                return;
            }

            loginRequest();
        }
    }

    private void loginRequest() {

        IDigitalService service = IDigitalClient.getClubService();
        Call<LoginResponse> call = service.postLogin("kcervan@idteam.pe", "123456");
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                Log.i(TAG, response.raw().toString());
                if (response.isSuccessful())
                    gotoRegisterActivity();
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void gotoRegisterActivity() {
        startActivity(new Intent(this, RegisterActivity.class));
    }
}
