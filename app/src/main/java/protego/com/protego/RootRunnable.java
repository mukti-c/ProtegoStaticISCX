package protego.com.protego;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStreamReader;

import RootTools.RootTools;

public class RootRunnable {


   static Handler runnableHandler;

    public static int inputLength;

   private static boolean refreshingActive = false;
   private static RootMethods rootMethods;

 static Context context;
   public static StringBuilder result=new StringBuilder();
    private TextView textView;


    RootRunnable(Context context,Activity activity,RootMethods rootMethods)
    {
        this.context=context;
        this.result=result;
        runnableHandler = new Handler();
        this.rootMethods=rootMethods;
        this.textView= (TextView) activity.findViewById(R.id.textView);
    }

    private static Runnable outputText = new Runnable() {
        @Override
        public void run() {

            byte buf[] = new byte[1024];



            try {
                if((rootMethods.dataInputStream.available()>0)==true)
                {
                    rootMethods.getDataInputStream().read(buf,0,1024);

                    result.append(new String(buf));
                    Toast.makeText(context,result,Toast.LENGTH_LONG).show();
                }
            } catch (IOException e) {
                stopRefreshing();
                e.printStackTrace();
            }



           runnableHandler.postDelayed(outputText,1000);
           Log.e("testing", "inside runnable");

        }
    };


    public static int start(String script)
    {



        if(rootMethods.start(script)==0)
        {
            startRefreshing();
            return 0;
        }

        return -1;

    }

    public static  int stop()
    {
        if(rootMethods.stop()==0) {
            Log.e("Stop Value","0");
            stopRefreshing();

            return 0;

        }
        return -6;
    }

    private static void startRefreshing()
    {
        if(!refreshingActive)
        {
            runnableHandler.post(outputText);
            refreshingActive= true;
        }

    }

    private static void  stopRefreshing()
    {

        if(refreshingActive)
        {
            runnableHandler.removeCallbacks(outputText);
            refreshingActive=false;
        }

    }


}



