package protego.com.protego;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import RootTools.RootTools;

public class App extends Activity {

    public void installTcpdumpBinary() {
        if(RootTools.installBinary(this, R.raw.tcpdump, "tcpdump")==false)
            Toast.makeText(getApplicationContext(), "Couldn't install tcpdump", Toast.LENGTH_SHORT).show();
    }

    private void copyFile (InputStream in, OutputStream out) {
        byte[] buffer = new byte[1024];
        int bytesRead;

        try {
            while ((bytesRead = in.read(buffer)) > 0)
                out.write(buffer, 0, bytesRead);
            in.close();
            out.close();

        } catch (IOException ioe) {
            Log.d("App", "Error in copying file.");
        }
    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new InitTask().execute();
    }

    private class InitTask extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progress;
        int flag;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = ProgressDialog.show(App.this, "", "Initializing...", true);
            progress.setCancelable(false);
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Initialize all global variables

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(App.this);
            if (!prefs.getBoolean("isClassifierBuilt", false)) {
                Tranny t1 = new Tranny();
                flag = t1.build();

                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("isClassifierBuilt", true);
                editor.commit();
            }

            if (!prefs.getBoolean("copiedTrainingDataset", false)) {
                InputStream in = null;
                OutputStream out = null;
                try {
                    String appFilesDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
                    String filename = "ISCX.arff";

                    in = getApplicationContext().getResources().openRawResource(R.raw.iscx);
                    File outFile = new File(appFilesDirectory, filename);
                    out = new FileOutputStream(outFile);
                    copyFile(in, out);
                    in.close();
                    in = null;
                    out.flush();
                    out.close();
                    out = null;

                    // Mark that tcpdump has been installed
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("copiedTrainingDataset", true);
                    editor.commit();
                } catch (IOException e) {
                    Log.d ("App", "File failed to copy");
                }
            }

            installTcpdumpBinary();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progress.dismiss();
            Intent i = new Intent("protego.com.protego.MAINACTIVITY");
            startActivity(i);
            if (flag == 0) {
                Toast.makeText(getApplicationContext(), "Classifier built :)", Toast.LENGTH_SHORT).show();
            } else if (flag == 1) {
                Toast.makeText(getApplicationContext(), "Error :( File not found", Toast.LENGTH_SHORT).show();
            } else if (flag == 2) {
                Toast.makeText(getApplicationContext(), "Error :( Classifier not built", Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }
}