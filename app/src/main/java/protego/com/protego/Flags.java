package protego.com.protego;

public class Flags {
    boolean CWR = false;
    boolean ECE = false;
    boolean URG = false;
    boolean ACK = false;
    boolean PSH = false;
    boolean RST = false;
    boolean SYN = false;
    boolean FIN = false;
    boolean none;
    boolean ORIGINATOR; // If the packet is from the originator, then T. Else, F.
}
