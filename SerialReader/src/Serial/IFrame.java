package Serial;

import java.util.ArrayList;

public class IFrame extends Frame {
	
	//This frame is used by the mote to indicate to the computer it is ready to receive data.
		private String _address;
		private ArrayList<Byte> _data;
		public IFrame(ArrayList<Byte> data) {
			_data = data;
			setType("Info");
			setData(data);
			_address = Integer.toHexString(data.get(1)&0xFF) + Integer.toHexString(data.get(2)&0xFF);
			
			//System.out.println();
			//System.out.println("https://github.com/openwsn-berkeley/openwsn-fw/blob/4a94cb63514b5bec6f438f339c76fdb436691998/inc/opendefs.h");
		}
		
		@Override
		public String toString(){
			String s = _address + " Info Frame: ";
			s = s+ " Code: " + Integer.toHexString(_data.get(4));
			return s;
		}
}
