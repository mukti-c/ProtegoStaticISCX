package protego.com.protego;

public class RootMethods extends Root {

     public RootMethods(){
        super();
    }

    public RootMethods(String script){

        start(script);
    }


    protected void finalize()
    {
        if(isProcessRunning()==true)
        {

        }
    }

    public static int stop()
    {
        if(runScript("killall tcpdump")!=0)
        return -5;
        return stopShell();
    }


    public static int start(String script)
    {
        if(startShell()==0) {
            runScript(script);
            return 0;
        }
        else
            return -1;
    }

}
