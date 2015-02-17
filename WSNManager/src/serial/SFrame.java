package serial;

import network_model.NeighborEntry;
import network_model.NeighborTable;

import java.awt.List;
import java.util.ArrayList;

public class SFrame extends Frame {
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
			//1 Byte Payload
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
			_toStringMessage = "SFrame decode debugNeighborEntry_t";
            NeighborEntry neighbor = new NeighborEntry();
            NeighborTable table = new NeighborTable();
            neighbor.row=_data.get(4);
            neighbor.used=_data.get(5);
            neighbor.parentPreference=_data.get(6);
            neighbor.stableNeighbor=_data.get(7);
            neighbor.switchStabilityCounter=_data.get(8);
            neighbor.addr_type=_data.get(9);
            switch(neighbor.addr_type)
            {
                case 0x00: //2
                	
                    neighbor.addr_16b = _data.subList(10,11).toArray(neighbor.addr_16b);
                    break;
                case 0x01: //8
                    neighbor.addr_64b = _data.subList(10,17).toArray(neighbor.addr_64b);
                    break;
                case 0x02: //16
                    neighbor.addr_128b = _data.subList(10,25).toArray(neighbor.addr_128b);
                    break;
                case 0x03: //2
                    neighbor.panid = _data.subList(10,11).toArray(neighbor.panid);
                    break;
                case 0x04: //8
                    neighbor.prefix = _data.subList(10,17).toArray(neighbor.prefix);
                    break;
            }
            // .....
            table.addRow(neighbor);
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
	private byte[] toByteArray(ArrayList<Byte> list)
    {
		//TODO:Implement method
		return null;
    }
	
	@Override
	public String toString(){
		return _address16 + " SFrame " + _statusType + " " + _toStringMessage;
	}
	

}
