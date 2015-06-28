import javax.xml.bind.DatatypeConverter;


public class Main {
	public Main(){
		
		String toConvert = "01001111 01110000 01100101 01101110 01010111 01010011 01001110 00100000 00110001 00101110 00111001 00101110 00110000 00001010 01010100 01100101 01101100 01101111 01110011 01000010 00001010 01001101 01010011 01010000 00110100 00110011 00110000 01100110 00110001 00110110 00110001 00110001 00001010 01000011 01000011 00110010 00110100 00110010 00110000";
		String[] b = toConvert.split(" ");
		byte[] d = new byte[b.length];
		for(int i = 0 ; i< b.length;i++){
			d[i]=(byte) Integer.parseInt(b[i], 2);
		}
		System.out.println(convert(d));
		
		
		
		
	}
	
	public String convert(byte[] data) {
	    StringBuilder sb = new StringBuilder(data.length);
	    for (int i = 0; i < data.length; ++ i) {
	        if (data[i] < 0) throw new IllegalArgumentException();
	        sb.append((char) data[i]);
	    }
	    return sb.toString();
	}
	
	public static void main(String[] args){
		new Main();
	}
}
