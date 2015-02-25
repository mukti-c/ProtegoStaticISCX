package protego.com.protego;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import org.jnetpcap.Pcap;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.packet.JPacketHandler;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.network.Icmp;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.network.Ip6;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


public class ReadFile1 extends Activity {

    private final static String TAG = "ReadFile";
    private final static String filename = Environment.getExternalStorageDirectory().getAbsoluteFile() + File.separator + "tcpdump.pcap";
    public final static String csvFile = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "ISCXConn.csv";

    private ArrayList<JPacket> packets = null;
    private Set<DataFromLog> connSet = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reader);

        packets = new ArrayList<JPacket>();
        connSet = new HashSet<DataFromLog>();

        File file = new File(csvFile);
        try {
            file.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        getPackets();
        if (connSet.size() != 0) {
            // Connection was going on when the pcap file got terminated
            CreateLogFile.logData.append(GetTime.getCurrentTime()+"Previous connection terminated\n");
            ISCXConnection.createConnectionRecord(connSet);
            CreateLogFile.logData.append(GetTime.getCurrentTime()+"Record for the terminated connection created\n");
            connSet.clear();
            GlobalVariables.clearVar();
        }
    }

    private void getPackets() {

        Log.d(TAG, "in getPackets()");
        StringBuilder errbuff = new StringBuilder();

        // Open parser in offline mode
        final Pcap parser = Pcap.openOffline(filename, errbuff);
        if (parser == null) {
            Log.d(TAG, filename + errbuff.toString());
        } else {
            Log.d(TAG, "Opened pcap file");

            JPacketHandler<String> handler = new JPacketHandler<String>() {
                DataFromLog temp;

                @Override
                public void nextPacket(JPacket packet, String user) {
                    // Add packet to displayArray and extract packet data
                    if (packet != null) {
                        Log.d(TAG, "Time started: " + System.nanoTime());
                        packets.add(packet);
                        temp = getPacketData(packet, new DataFromLog());
                        addToConnSet(temp, connSet);
                    }
                }
            };

            parser.loop(-1, handler, null);
            parser.close();
        }
    }

    private void addToConnSet(DataFromLog data, Set<DataFromLog> connSet) {
        if (data.PROTOCOL != null && data.SERVICE != null && data.DEST_IP != null && data.SRC_IP != null) {
            if (connSet.size() == 0) {
                GlobalVariables.connProtocol = data.PROTOCOL;
                GlobalVariables.connService = data.SERVICE;
                GlobalVariables.connSourceIP = data.SRC_IP;
                GlobalVariables.connDestIP = data.DEST_IP;
                GlobalVariables.connSourcePort = data.SRC_PORT;
                GlobalVariables.connDestPort = data.DEST_PORT;
                GlobalVariables.startTime = data.TIMESTAMP;
                GlobalVariables.endTime = data.TIMESTAMP;
                connSet.add(data);
                CreateLogFile.logData.append(GetTime.getCurrentTime()+"New Connection set created.\nOngoing Connection.\n");
            } else {
                if (GlobalVariables.connProtocol.equals(data.PROTOCOL)
                        && GlobalVariables.connService.equals(data.SERVICE)
                        && ((GlobalVariables.connDestIP.equals(data.DEST_IP) && GlobalVariables.connSourceIP.equals(data.SRC_IP)) || (GlobalVariables.connDestIP.equals(data.SRC_IP) && GlobalVariables.connSourceIP.equals(data.DEST_IP)))) {
                    // The packet is from the same connection
                    GlobalVariables.endTime = data.TIMESTAMP;
                    connSet.add(data);
                } else {
                    GlobalVariables.endTime = data.TIMESTAMP;
                    CreateLogFile.logData.append(GetTime.getCurrentTime()+"Previous connection terminated\n");
                    ISCXConnection.createConnectionRecord(connSet);
                    CreateLogFile.logData.append(GetTime.getCurrentTime()+"Record for the terminated connection created\n");
                    connSet.clear();
                    GlobalVariables.clearVar();
                    GlobalVariables.startTime = data.TIMESTAMP;
                    GlobalVariables.connProtocol = data.PROTOCOL;
                    GlobalVariables.connService = data.SERVICE;
                    GlobalVariables.connSourceIP = data.SRC_IP;
                    GlobalVariables.connDestIP = data.DEST_IP;
                    GlobalVariables.connSourcePort = data.SRC_PORT;
                    GlobalVariables.connDestPort = data.DEST_PORT;
                    connSet.add(data);
                    CreateLogFile.logData.append(GetTime.getCurrentTime()+"New Connection set created\n");
                }
            }
        }
    }

    private DataFromLog getPacketData(JPacket packet, DataFromLog data) {

        // Types of packets that can be detected
        Ip4 ip4 = new Ip4();
        Ip6 ip6 = new Ip6();
        Tcp tcp = new Tcp();
        Udp udp = new Udp();
        Icmp icmp = new Icmp();

        if (packet != null) {
            data.TIMESTAMP = new Date(packet.getCaptureHeader().timestampInMillis());

            if (packet.hasHeader(ip4.ID)) {
                packet.getHeader(ip4);
                data.SRC_IP = FormatUtils.ip(ip4.source());
                data.DEST_IP = FormatUtils.ip(ip4.destination());

                // Check which protocol is being used
                if (packet.hasHeader(tcp.ID)) {
                    packet.getHeader(tcp);
                    data.PROTOCOL = "tcp";
                    data.SRC_PORT = tcp.source();
                    data.DEST_PORT = tcp.destination();
                    data.SERVICE = data.assignService();
                    data.LENGTH = tcp.getPayloadLength() + tcp.getHeaderLength();
                    data.PAYLOAD = tcp.getPayload();
                } else if (packet.hasHeader(udp.ID)) {
                    packet.getHeader(udp);
                    data.PROTOCOL = "udp";
                    data.SRC_PORT = udp.source();
                    data.DEST_PORT = udp.destination();
                    data.SERVICE = data.assignService();
                    data.LENGTH = udp.getPayloadLength() + udp.getHeaderLength();
                    data.PAYLOAD = udp.getPayload();
                }
            } else if (packet.hasHeader(icmp.ID)) {
                packet.getHeader(icmp);
                data.PROTOCOL = "icmp";
                data.SRC_IP = "unknown";
                data.DEST_IP = "unknown";

                // Further, ICMP "ports" are to be interpreted as the source port meaning the ICMP message type and
                // the destination port being the ICMP message code.
                // Source: https://www.bro.org/sphinx/_downloads/main20.bro
                data.SRC_PORT = icmp.type();
                data.DEST_PORT = icmp.code();
                data.SERVICE = data.assignIcmpService(data.PROTOCOL, data.SRC_PORT, data.DEST_PORT);
                data.LENGTH = icmp.getPayloadLength() + icmp.getHeaderLength();
                data.PAYLOAD = icmp.getPayload();
            } else if (packet.hasHeader(ip6.ID)) {
                packet.getHeader(ip6);
                data.SRC_IP = FormatUtils.asStringIp6(ip6.source(), true);
                data.DEST_IP = FormatUtils.asStringIp6(ip6.destination(), true);

                // Check which protocol is being used
                if (packet.hasHeader(tcp.ID)) {
                    packet.getHeader(tcp);
                    data.PROTOCOL = "tcp";
                    data.SRC_PORT = tcp.source();
                    data.DEST_PORT = tcp.destination();
                    data.SERVICE = data.assignService();
                    data.LENGTH = tcp.getPayloadLength() + tcp.getHeaderLength();
                    data.PAYLOAD = tcp.getPayload();
                } else if (packet.hasHeader(udp.ID)) {
                    packet.getHeader(udp);
                    data.PROTOCOL = "udp";
                    data.SRC_PORT = udp.source();
                    data.DEST_PORT = udp.destination();
                    data.SERVICE = data.assignService();
                    data.LENGTH = udp.getPayloadLength() + udp.getHeaderLength();
                    data.PAYLOAD = udp.getPayload();
                }
            }
        }
        return data;
    }
}