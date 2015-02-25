package protego.com.protego;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ClassifyMain extends Activity implements View.OnClickListener{
    Button classify, eval;
    public String out1;
    TextView filecon, textView;
    int counter = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.classify_main);

        classify = (Button) findViewById(R.id.beval);
        eval = (Button) findViewById(R.id.eval);
        filecon = (TextView) findViewById(R.id.filecon);
        textView = (TextView) findViewById(R.id.textView);

        textView.setOnClickListener(this);

        classify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Tranny t1 = new Tranny();
                String out = t1.classify();
                filecon.setText(out);
            }
        });

        eval.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                new EvalTask().execute();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onClick(View v)
    {

        if(v == textView) {
            counter++;
        }
        if(counter==7)
        {
            Toast.makeText(getApplicationContext(), "Yo mama is so fat, that her weight is more than the lines of code!", Toast.LENGTH_LONG) .show();
        }
        else if(counter==14)
        {
            Toast.makeText(getApplicationContext(), "Says THE BATMAN!", Toast.LENGTH_LONG).show();
            counter = 0;
        }
    }

    private class EvalTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog progress;
        @Override
        protected Void doInBackground(Void... params) {
            Tranny t1 = new Tranny();
            out1 = t1.evaluate();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = ProgressDialog.show(ClassifyMain.this, "", "Please wait while the model is evaluated...", true);
            progress.setCancelable(false);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progress.dismiss();
            filecon.setText(out1);
        }
    }
}