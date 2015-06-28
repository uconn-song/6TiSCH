package serial;

import network_model.NeighborEntry;
import network_model.WSNManager;

import java.util.ArrayList;

public class SFrame extends Frame {
	//S frame relays root info
	public static boolean ROOT_SET =false;
	//composed of 2 byte address, 1 byte type, payload depending on type
	public String _statusType;
	public String _address16;
	//status packet information
	public boolean _isSynched;
	private String _toStringMessage="";
	public SFrame(ArrayList<Byte> data) {
		setType("Status");
		setData(data);
		setVariables();
	}
	private void setVariables() {
		_address16 = Integer.toHexString((_data.get(1)&0xFF)) + Integer.toHexString(_data.get(2)&0xFF);
		//System.out.println(_address16 +"  "+ (_data.get(1)&0xFF) + "." + (_data.get(2)&0xFF)+ " address");
		switch((int)(_data.get(3)&0xFF)){
		case 0:
			_statusType = "0 ISSYNC";
	
			if(_data.get(4)==1){
				_isSynched = true;
				_toStringMessage ="Sync = true";
			}else{
				_isSynched = false;
				_toStringMessage ="Sync = false";
			}
			
			//1 byte payload, 1 is synched, 0 is not synched
			break;
		case 1:
			_statusType = "1 ID";
			_toStringMessage = "SFrame handle debugIDManagerEntry_t";
			break;
		case 2:
			_statusType = "2 DAGRANK";
			_toStringMessage = " "+ ( ((_data.get(3) & 0xFF) << 8) | (_data.get(4) & 0xFF));
			//this.byteToString(_data.get(3))+this.byteToString(_data.get(4))+
			//1 Byte Payload serial data invalid
			break;
		case 3:
			_statusType = "3 OUTBUFFERINDEXES";
			//2 Bytes payload
			break;
		case 4:
			_statusType = "4 ASN";
			_toStringMessage = "SFrame handle asn_t";
			break;
		case 5:
			_statusType = "5 MACSTATS";
			_toStringMessage = "SFrame handle ieee154e_stats_t";
			break;
		case 6:
			_statusType = "6 SCHEDULE";
			_toStringMessage = "SFrame handle debugScheduleEntry_t";
			break;
		case 7:
			_statusType = "7 BACKOFF";
			break;
		case 8:
			_statusType = "8 QUEUE";
			_toStringMessage = "SFrame Queue debugOpenQueueEntry_t";
			break;
		case 9:
			_statusType = "9 NEIGHBORS";
			_toStringMessage = "SFrame debugNeighborEntry_t";
			break;
		case 10:
			_statusType = "10 KAPERIOD ? ";
			//System.out.println("unsure of this message, first seen when trying to write to mote");
			break;
		default:
			_toStringMessage = "StatusFrame invalid type " + (int)(_data.get(3)&0xFF);
			break;
		
			
		}
	}
	
	
	/** 
	 * @return NeighborEntry method for getting the DAG root neighbors
	 */
	public NeighborEntry parseNeighbors(){
		if(!_statusType.startsWith("9")) throw new IllegalArgumentException(" can't parse " + _statusType + " as neighbor entry");
         return new NeighborEntry(_data);
	}
	
	/**
	 * on message with status type 1 set the root prefix
	 */
	public void getRootPrefix(WSNManager m){
		if(_data.get(4)==(byte)1&&!ROOT_SET){
			byte[] b = new byte[8];
			for(int i = 0 ; i<8;i++){
				b[i] = _data.get(i+9);
			}
			m.setRoot(b);
			//System.out.println("I AM ROOT");
			ROOT_SET=true;
		}
	}
	
	
	
	
	private byte[] toByteArray(ArrayList<Byte> list)
    {
		//TODO:Implement method
		return null;
    }

    private short bytesToShort(byte[] A)
    {
        if(A.length != 2)
        {
            throw new RuntimeException("Array not 2 bytes");
        }
        else
        {
            short byte1 = (short) (A[0] & 0xFF);
            short byte2 = (short) (A[1] & 0xFF);
            byte1 = (short) (byte1 << 8);
            return (short) (byte1 ^ byte2);
        }
    }

	
	@Override
	public String toString(){
		return _address16 + " SFrame " + _statusType + " " + _toStringMessage;
	}
	

}
