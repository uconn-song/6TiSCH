package network_model;

import java.util.Arrays;

/**
 * Created by Timothy on 2/13/2015.
 */
public class NeighborEntry
{
    //this is basically mimicing a struct right now.
    //I might change this because it would be nice to iterate over these fields.
    public byte row;
    public byte used;
    public byte parentPreference;
    public byte stableNeighbor;
    public byte switchStabilityCounter;
    public byte addr_type;
    //public byte addr_bodyH; // not entirely clear on these. it looks like there are 5 types of addresses that could be here so I want to figure out how to interpret the type to figure out how long it will be and take that many following bytes and put them in an array.
    //public byte addr_bodyL; // the bodyH, bodyL thing is from the Python parser, but I'm not really sure how that works
    public Byte[] addr_16b = new Byte[2];
    public Byte[] addr_64b = new Byte[8];
    public Byte[] addr_128b = new Byte[16];
    public Byte[] panid = new Byte[2]; // 2 bytes
    public Byte[] prefix = new Byte[8]; // 8 bytes

    public byte DAGrank;
    public byte rssi;
    public byte numRx;
    public byte numTx;
    public byte numTxACK;
    public byte numWraps;
    public byte asn_4;
    public byte asn_2_3;
    public byte asn_0_1;
    public byte joinPrio;

    public NeighborEntry(){}

    @Override
    public String toString()
    {
    	String ret= "Neighbor Entry: ";
    	
    	switch(addr_type)
        {
            case 0x00: //2
            	ret = ret + " addr_16 " + byteArrayToString(addr_16b,"hex");
                break;
            case 0x01: //8
            	ret = ret + "  addr 64b  " + byteArrayToString(addr_64b,"hex");
                break;
            case 0x02: //16
            	ret = ret + "  addr 128b  " + byteArrayToString(addr_128b, "hex");
                break;
            case 0x03: //2
            	ret = ret+ "  panid " + Arrays.toString(panid);  
                break;
            case 0x04: //8
            	ret = ret + "  prefix  " + Arrays.toString(prefix);
                break;
        }    	
        return ret;
    }

    @Override
    public boolean equals(Object obj)
    {
        return this.row == ((NeighborEntry)obj).row;
    }
    
    public String byteArrayToString(Byte[] byteArr,String base ){
    	String s = "[";
    	switch(base){
    	case "hex":
    		for(int i = 0 ; i < byteArr.length;i++){
    			s = s+" " + Integer.toHexString((byteArr[i]&0xFF));
    		}
    		return s + "] ";
    	default:
    		return byteArr.toString();
    	}
    	
    }
}
