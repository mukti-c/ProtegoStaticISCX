package protego.com.protego;

import android.util.Base64;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.UnsupportedEncodingException;
import java.util.Set;

public class ISCXConnection {
    // Features
    String appName = "";
    int totalSourceBytes = 0;
    int totalDestinationBytes = 0;
    int totalDestinationPackets = 0;
    int totalSourcePackets = 0;
    String sourcePayloadAsBase64 = "";
    String destinationPayloadAsBase64 = "";
    String destinationPayloadAsUTF = "";
    String direction = "";
    String sourceTCPFlagsDescription = "";
    String destinationTCPFlagsDescription = "";
    String source = "";
    String protocolName = "";
    int sourcePort = 0;
    String destination = "";
    int destinationPort = 0;
    String startDateTime = "";
    String stopDateTime = "";

    private String convertRecord() {
        return (this.appName
                + "," + this.totalSourceBytes
                + ',' + this.totalDestinationBytes
                + ',' + this.totalDestinationPackets
                + ',' + this.totalSourcePackets
                + ',' + this.sourcePayloadAsBase64
                + ',' + this.destinationPayloadAsBase64
                + ',' + this.destinationPayloadAsUTF
                + ',' + this.direction
                + ',' + this.sourceTCPFlagsDescription
                + ',' + this.destinationTCPFlagsDescription
                + ',' + this.source
                + ',' + this.protocolName
                + ',' + this.sourcePort
                + ',' + this.destination
                + ',' + this.destinationPort
                + ',' + this.startDateTime
                + ',' + this.stopDateTime);
    }

    public static void writeToARFF(String filename, ISCXConnection object) {
        // Filename is the name of the ARFF file to which the connection record is to be appended.
        try {
            File file = new File(filename);
            FileWriter writer = new FileWriter(file, true);
            //Log.d ("Record", object.convertRecord());
            writer.write(object.convertRecord()+"\n");
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Creates a connection
    public static void createConnectionRecord(Set<DataFromLog> logData) {
        ISCXConnection newConn = new ISCXConnection();
        //SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-DD hh:mm:ss");

        //AppName can be found using iptables
        newConn.appName = "HTTPWeb";
        newConn.direction = (GlobalVariables.connSourceIP.equals(GlobalVariables.connDestIP)) ? "L2L" : "L2R";
        newConn.source = GlobalVariables.connSourceIP;
        newConn.protocolName = GlobalVariables.connProtocol + "_ip";
        newConn.sourcePort = GlobalVariables.connSourcePort;
        newConn.destination = GlobalVariables.connDestIP;
        newConn.destinationPort = GlobalVariables.connDestPort;
        newConn.startDateTime = GlobalVariables.startTime.toString();
        //newConn.startDateTime = sdf.format(GlobalVariables.startTime);
        //newConn.stopDateTime = sdf.format(GlobalVariables.endTime);
        newConn.stopDateTime = GlobalVariables.endTime.toString();

        for (DataFromLog temp: logData) {
            newConn.totalSourceBytes += (temp.SRC_IP.equals(GlobalVariables.connSourceIP)) ? temp.LENGTH : 0;
            newConn.totalSourcePackets += (temp.SRC_IP.equals(GlobalVariables.connSourceIP)) ? 1 : 0;
            newConn.totalDestinationBytes += (temp.DEST_IP.equals(GlobalVariables.connSourceIP)) ? temp.LENGTH : 0;
            newConn.totalDestinationPackets += (temp.DEST_IP.equals(GlobalVariables.connSourceIP)) ? 1 : 0;
            newConn.sourcePayloadAsBase64 += (temp.SRC_IP.equals(GlobalVariables.connSourceIP)) ? new String(Base64.encode(temp.PAYLOAD, Base64.DEFAULT)) : "";
            newConn.destinationPayloadAsBase64 += (temp.DEST_IP.equals(GlobalVariables.connSourceIP)) ? new String(Base64.encode(temp.PAYLOAD, Base64.DEFAULT)) : "";
            try {
                newConn.destinationPayloadAsUTF += (temp.DEST_IP.equals(GlobalVariables.connSourceIP)) ? new String(temp.PAYLOAD, "utf-8") : "";
            } catch (UnsupportedEncodingException e) {
                Log.d ("UTF conversion", e.getStackTrace().toString());
            }
        }
        writeToARFF(ReadFile1.csvFile, newConn);
    }
}
