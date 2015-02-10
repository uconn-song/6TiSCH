package serial;

import java.util.ArrayList;

public class DFrame extends Frame {

		//This frame is used by the mote to indicate to the computer it is ready to receive data.
		private String _address="";
		public DFrame(ArrayList<Byte> data) {
			setType("Data");
			setData(data);
			data.get(0);//"D"
			_address =  Integer.toHexString(data.get(2)&0xFF)+ Integer.toHexString(data.get(1)&0xFF);//address part 2
			data.get(3);//asn 0 
			data.get(4);//asn 1
			data.get(5);//asn 2
			data.get(6);//asn 3
			data.get(7);//asn 4
			for(int i = 8 ; i < data.size()-2;i++)
			{
				//System.out.print("Data: " + byteToString(data.get(i))+"(0x"+Integer.toHexString(data.get(i)&0xFF)+") ");
			}
			//System.out.println();
			//HDLC.crcValidate(data);
			System.out.println();

		}
		
		@Override
		public String toString(){
			String toret= _address + " sent: ";
			
			if(_data.size()<15){
			for(int i = 8 ; i < _data.size()-2;i++)
			{
				toret = toret+" " + byteToString(_data.get(i)) + "("+(char)(_data.get(i)&0xFF)+")" ;
			}
			}else{
				for(int i = 8 ; i < _data.size()-2;i++)
				{
					toret = toret+" " + byteToString(_data.get(i));
				}
			}
			return toret;
		}
	

}
