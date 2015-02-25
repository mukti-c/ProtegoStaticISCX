package protego.com.protego;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


public class ScriptRunner extends Thread{
    private String script;
    private StringBuilder result;
    private Process process;
    public int exitValue;
    boolean EndScript=false;

    public ScriptRunner(String script,StringBuilder result)
    {
        this.script=script;
        this.result=result;
    }

    @Override

    public void run() {
        super.run();
        try {
            process = Runtime.getRuntime().exec("su");
            OutputStreamWriter outWriter= new OutputStreamWriter(process.getOutputStream());
            outWriter.write(script);
            if(!script.endsWith("\n"))
                outWriter.write("\n");
            outWriter.flush();
            outWriter.write("exit \n");
            outWriter.flush();
            char buf[]=new char[1024];
            int inputLength;
            InputStreamReader reader= new InputStreamReader(process.getInputStream());
            if((inputLength = reader.read(buf)) != -1)
            {
                if(result!=null)
                    result.append(buf,0,inputLength);

            }

            reader= new InputStreamReader(process.getErrorStream());
            if((inputLength=reader.read(buf))!=-1)
            {
                if(result!=null)
                    result.append(buf,0,inputLength);
            }
            if (result!=null) {
                this.exitValue = process.waitFor();

            }

        } catch (IOException e) {
            e.printStackTrace();
            result.append("\n"+e);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
            result.append("\nConnection timed out");
        }
        finally {
            if(process!=null)
                process.destroy();
            process=null;

        }

    }
}

