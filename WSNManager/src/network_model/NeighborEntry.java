package network_model;

import java.util.ArrayList;
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

    public short DAGrank;
    public byte rssi;
    public byte numRx;
    public byte numTx;
    public byte numTxACK;
    public byte numWraps;
    public byte asn_4;
    public short asn_2_3;
    public short asn_0_1;
    public byte joinPrio;
    
    private String _iid64Hex;
   // coap://141592000013cb23/n GET
    public NeighborEntry(ArrayList<Byte> _data){
    	
    	row=_data.get(4);
        used=_data.get(5);
        
        //if not used forget about parsing the rest of this message
        if(used ==0)
        	return;
        
        parentPreference=_data.get(6);
        stableNeighbor=_data.get(7);
        switchStabilityCounter=_data.get(8);
        //addr_type=_data.get(9);
        addr_type = 0x01;
        int index = 10;
        switch(addr_type)
        {
            case 0x00: //2
                addr_16b = _data.subList(10,12).toArray(addr_16b);
                index = 13;
                break;
            case 0x01: //8
                addr_64b = _data.subList(10,18).toArray(addr_64b);
                index = 19;
                break;
            case 0x02: //16
                addr_128b = _data.subList(10,26).toArray(addr_128b);
                index = 27;
                break;
            case 0x03: //2
                panid = _data.subList(10,12).toArray(panid);
                index = 13;
                break;
            case 0x04: //8
                prefix = _data.subList(10,18).toArray(prefix);
                index = 19;
                break;
        }

        _iid64Hex = "";
        for(int i = 0 ; i < 8;i++){
        	 _iid64Hex =  _iid64Hex + Integer.toHexString(( addr_64b[i]&0xFF));
		}
        
        
        short byte1 = (short)(_data.get(index++)&0xFF);
        short byte2 = (short)(_data.get(index++)&0xFF);
        byte1 = (short)(byte1<<8);
        DAGrank = (short)(byte1 ^ byte2);
        rssi = _data.get(index++);
        numRx = _data.get(index++);
        numTx = _data.get(index++);
        numTxACK = _data.get(index++);
        numWraps = _data.get(index++);
        asn_4 = _data.get(index++);
        byte1 = (short)(_data.get(index++)&0xFF);
        byte2 = (short)(_data.get(index++)&0xFF);
        byte1 = (short)(byte1<<8);
        asn_2_3 = (short)(byte1 ^ byte2);
        byte1 = (short)(_data.get(index++)&0xFF);
        byte2 = (short)(_data.get(index++)&0xFF);
        byte1 = (short)(byte1<<8);
        asn_0_1 = (short)(byte1 ^ byte2);
        joinPrio = _data.get(index);
        
        
        
        
    }
    
   /**
    * neighbor entry constructor for CoAP payload
    * @param _data
    */
 public NeighborEntry(byte[] _data){
    	System.out.println("parsing neighbor entry");
    	row=_data[1]; //1
        used=_data[2]; //2
        
        //if not used forget about parsing the rest of this message
        if(used ==0)
        	return;
        
        parentPreference=_data[3]; //3
        stableNeighbor=_data[4]; //4
        switchStabilityCounter=_data[5];//5
        //addr_type=_data.get(9);
        addr_type = 0x01;
        int index = 10;
        
        for(int i = 0 ; i < 8;i++){
        	addr_64b[i] = _data[i+7];
        }
        

        _iid64Hex = "";
        for(int i = 0 ; i < 8;i++){
        	 _iid64Hex =  _iid64Hex + Integer.toHexString(( addr_64b[i]&0xFF));
		}
        
        
        short byte1 = (short)(_data[15]&0xFF);
        short byte2 = (short)(_data[16]&0xFF);
        byte1 = (short)(byte1<<8);
        DAGrank = (short)(byte1 ^ byte2);
        rssi = _data[17];
        numRx = _data[18];
        numTx = _data[19];
        numTxACK = _data[20];
        numWraps = _data[21];
        asn_4 = _data[22];
        byte1 = (short)(_data[23]&0xFF);
        byte2 = (short)(_data[24]&0xFF);
        byte1 = (short)(byte1<<8);
        asn_2_3 = (short)(byte1 ^ byte2);
        byte1 = (short)(_data[25]&0xFF);
        byte2 = (short)(_data[26]&0xFF);
        byte1 = (short)(byte1<<8);
        asn_0_1 = (short)(byte1 ^ byte2);
        joinPrio = _data[27];
        
        
        
        
    }

    public String getiid64Hex(){
    	return _iid64Hex;
    }
    @Override
    public String toString()
    {
    	String ret= "Neighbor Entry: ";
    	ret = ret + "\nrow: " + row;
        ret = ret + "\nused: " + used;
        ret = ret + "\nparentPreference: " + parentPreference;
        ret = ret + "\nstableNeighbor: " + stableNeighbor;
        ret = ret + "\nswitchStabilityCounter: " + switchStabilityCounter+ "\n";


//
//        switch(addr_type)
//        {
//            case 0x00: //2
//            	ret = ret + " addr_16 " + byteArrayToString(addr_16b,"hex");
//                break;
//            case 0x01: //8
            	ret = ret + "  addr 64b  " + byteArrayToString(addr_64b,"hex");
//                break;
//            case 0x02: //16
//            	ret = ret + "  addr 128b  " + byteArrayToString(addr_128b, "hex");
//                break;
//            case 0x03: //2
//            	ret = ret+ "  panid " + Arrays.toString(panid);  
//                break;
//            case 0x04: //8
//            	ret = ret + "  prefix  " + Arrays.toString(prefix);
//                break;
//        }
        ret = ret + "\nDAGrank: " + DAGrank;
        ret = ret + "\nrssi: " + rssi;
        ret = ret + "\nnumRx: " + numRx;
        ret = ret + "\nnumTx: " + numTx;
        ret = ret + "\nnumTxACK: " + numTxACK;
        ret = ret + "\nnumWraps: " + numWraps;
        ret = ret + "\nasn_4: " + asn_4;
        ret = ret + "\nasn_2_3: " + asn_2_3;
        ret = ret + "\nasn_0_1: " + asn_0_1;
        ret = ret + "\njoinPrio: " + joinPrio;
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
