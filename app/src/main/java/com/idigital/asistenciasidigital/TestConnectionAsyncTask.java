package com.idigital.asistenciasidigital;

import android.os.AsyncTask;

import java.io.IOException;

/**
 * Created by USUARIO on 26/04/2017.
 */

public abstract class TestConnectionAsyncTask extends AsyncTask<Void, Void, Boolean> {

    @Override
    final protected Boolean doInBackground(Void... progress) {
        // do stuff, common to both activities in here
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return  (exitValue == 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }
}
