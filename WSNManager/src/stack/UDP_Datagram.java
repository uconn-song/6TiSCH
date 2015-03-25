package stack;

/*
 *  			  0      7 8     15 16    23 24    31
                 +--------+--------+--------+--------+
                 |     Source      |   Destination   |
                 |      Port       |      Port       |
                 +--------+--------+--------+--------+
                 |                 |                 |
                 |     Length      |    Checksum     |
                 +--------+--------+--------+--------+
 */
public class UDP_Datagram {
	private int _sourcePort;//2 byte
	private int _destinationPort;//2 byte
	private int _length;// 2 bytes specify length of datagram >8 bytes
	private int _checksum = 0;// 2 bytes
	private byte[] _payload;
	
	
	public byte[] _destAddr64;
	
	/**
	 * for parsing received messages
	 */
	public UDP_Datagram(byte[] b) {
		_sourcePort = ((b[0]&0xFF)<<8)+(b[1]&0xFF);
		System.out.println(Integer.toBinaryString(_sourcePort));
		_destinationPort = (
				(b[2]&0xFF)<<8)+(b[3]&0xFF);
		System.out.println(_destinationPort + " " + Integer.toBinaryString(_destinationPort));
		_length = ((b[4]&0xFF)<<8) + b[5]&0xFF;
		_payload = new byte[b.length-8];
		for(int i =0;i<b.length-8;i++){
			_payload[i]=b[i+8];
		}
		// compute checksum
		checksumIteration(_sourcePort);
		checksumIteration(_destinationPort);
		checksumIteration(_length);
		
		if (_payload.length % 2 == 0) {
			for (int i = 0; i < _payload.length; i = i + 2) {
				checksumIteration(((_payload[i]&0xFF) << 8) + _payload[i + 1]&0xFF);
			}
		} else {
			
			for (int i = 0; i < _payload.length-1; i = i + 2) {
				checksumIteration(((_payload[i]&0xFF) << 8) + _payload[i + 1]&0xFF);
			}
			
			checksumIteration((_payload[_payload.length-1]&0xFF)<<8);
		}
		
		_checksum = _checksum ^ 0xFFFF;
		
		//System.out.println(_checksum);
		int check = (b[6]&0xFF)<<8|(b[7]&0xFF);
	//	System.out.println(check);
		
		
		//printRaw(b);
		
		//System.out.println(byteToString((byte) ((_checksum >> 8) & 0xFF))
		//		+ byteToString((byte) (_checksum & 0xFF)));
	}
	
	
	//for sending messages
	public UDP_Datagram(CoapMessage m) {
		_sourcePort = m.sourcePort;
		_destinationPort = m.destPort;
		_payload = m.getMessage();
		_length = _payload.length + 8;
		_destAddr64 = m._destIID64;
		// compute checksum
		checksumIteration(_sourcePort);
		checksumIteration(_destinationPort);
		checksumIteration(_length);
		
		if (_payload.length % 2 == 0) {
			for (int i = 0; i < _payload.length; i = i + 2) {
				checksumIteration((_payload[i] << 8) + _payload[i + 1]);
			}
		} else {
			
			for (int i = 0; i < _payload.length-1; i = i + 2) {
				checksumIteration((_payload[i] << 8) + _payload[i + 1]);
			}
			
			checksumIteration(_payload[_payload.length-1]<<8);
		}
		
		_checksum = _checksum ^ 0xFFFF;
		}

	private void checksumIteration(int b) {

		_checksum = _checksum + b;
		if ((_checksum & 0b1_0000000000000000) != 0) {
			_checksum = (_checksum & 0xFFFF) + 1;
		}
	}

	
	
	public byte[] getMessage(){
		byte[] b = new byte[8+_payload.length];
		b[0] = (byte) (_sourcePort>>8&0xFF);
		b[1] = (byte) (_sourcePort&0xFF);
		b[2] = (byte) (_destinationPort>>8&0xFF);
		b[3] = (byte) (_destinationPort&0xFF);
		b[4] = (byte) (_length>>8&0xFF);
		b[5] = (byte) (_length&0xFF);
		b[6] = (byte) (_checksum>>8&0xFF);
		b[7] = (byte) (_checksum &0xFF);
		for(int i = 0 ; i< _payload.length;i++){
			b[i+8] = _payload[i];
		}
		return b;
	}
	
	public void printRaw(byte[] b){
		for(int i = 0 ; i < b.length;i++){
			if(i==8){
				System.out.println("Payload:");
			}
			if(i>0 && i%4==0.0)
			{
				System.out.println();
			}
			System.out.print(byteToString(b[i]) + " ");
			//every 4 bytes new line
		}
		System.out.println();
	}
	private String byteToString(byte b) {
		return String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(
				' ', '0');
	}


}
