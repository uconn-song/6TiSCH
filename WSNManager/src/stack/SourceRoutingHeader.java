package stack;

import graphStream.NetworkGraph;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import org.graphstream.algorithm.Dijkstra;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;
import org.graphstream.graph.implementations.MultiGraph;

import network_model.NetworkModel;

public class SourceRoutingHeader {
	private byte[] headerBytes;
	
	private byte[] nextHop;
	
	
	
	/*
	   Structure in openwsn  As defined in http://tools.ietf.org/html/rfc6554#section-3
	   typedef struct {
	   uint8_t    nextHeader;    ///< Header immediately following.
	   uint8_t    HdrExtLen;     ///< In 8-octet units, excluding first 8.
	   uint8_t    RoutingType;   ///< Set to 3 for "Source Routing Header".
	   uint8_t    SegmentsLeft;  ///< Number of addresses still to visit.
	   uint8_t    CmprICmprE;    ///< Number of prefix octets elided for all (CmprI) and last (CmprE) segment
	   uint8_t    PadRes;        ///< Number of padding octets. Set to 0 if using EUI64.
	   uint16_t   Reserved;      ///< Set to 0.
		} rpl_routing_ht;
	 */
	
	
	
	
	
	
	
	/**
	 * assume 8 prefix bytes elided from all addresses in path, assuming next header is UDP header
	 * @param _model
	 * @param sourceRouteGraph
	 * @param dest64
	 */
	public SourceRoutingHeader(NetworkModel _model, NetworkGraph sourceRouteGraph, String dest64) {
	
			
		 /*
		    * Build path backwards
		    * possible algorithm
		    * 	start from destination mote and find preferred parent and record
		    * 		verify that path is not blocked by manager, otherwise choose neighbor with lowest
		    *   find preferred parent of that mote and record in arraylist
		    *   repeat until reaching root.
		    */
		   
		   byte nextHeader = 0x11; //udp is the next header
		   
		   
		   //begin building source routing path
		   List<Node> path = dijkstraPathDiscovery(_model,dest64,sourceRouteGraph);
		   //path returns a 
		   

		   if(path.size()<2) throw new IllegalArgumentException("dijkstra source route path too short");
		   
		   
		   
		   //store first hop to be set in destination of packet
		   if (path.get(1).getId().length() == 16) {
				// try to parse address
				nextHop = DatatypeConverter.parseHexBinary(path.get(1).getId());
			
			} else {
				System.out.println("SOURCE ROUTING HEADER: ERROR IN ADDR LENGTH " + path.get(1).getId());
			}
		   
		   
		   
		   
		   /* 8-bit selector.  Identifies the particular
           Routing header variant.  An SRH should set the
           Routing Type to 3.*/
		   byte RoutingType = 0x03; 
		   
		   //prefix octets elided for addresses except final destination
		   byte local_CmprI = 8; 
		   
		   //prefix octets elided for last address	
		   byte local_CmprE = 8;
		   
		   //4 bit 8 followed by 4 bit 8
		   byte elidedBytes = (byte) 0x88;
		   
		  /* 8-bit unsigned integer.  Length of the Routing
           header in 8-octet units, not including the first
           8 octets.  Note that when Addresses[1..n] are
           compressed (i.e., value of CmprI or CmprE is not
           0), Hdr Ext Len does not equal twice the number
           of Addresses.*/
		   
		   
		   int hopsLeft = path.size()-2;
		   
		   
		   
		   //the path contains the source  address, the source address is not used
		   //in this header so we subtract one from HdrExtLen.
		   //In addition, the first hop's address is put into the destination address in the IPv6 header
		   //or in our case the compressed IPv6 header reducing the number of addresses in this header
		   //yet again. The second hop to the final destination
		   //is  therefore all that is required in this header.
		   
		   //at least one hop will be the last mote with the following length
		   int  hdr_ext_len = (16-local_CmprE) + (hopsLeft-1)*(16-local_CmprI);
		   
		  
		   
		   
		   /*
		   8-bit unsigned integer.  Number of route segments
           remaining, i.e., number of explicitly listed
           intermediate nodes still to be visited before
           reaching the final destination.  The originator
           of an SRH sets this field to n, the number of
           addresses contained in Addresses[1..n].
           In this case it is path.size()-2 because path includes source and destination*/
		   byte segmentsLeft	 = (byte) hopsLeft;
		   
		   
		   byte padReserved0 = 0;
		   byte reserved1 =0;
		   byte reserved2=0;
		   
		   int baseSize = 8; //8 bytes 
		   int hopsSize = (segmentsLeft-1)*(16-local_CmprI) + (16-local_CmprE);
		   
		   
		   if(hopsLeft==0){
			   hdr_ext_len = 0;
			   hopsSize=0;
			   segmentsLeft=0;
		   }
		   
		   
		   
		   byte[] sourceRouteHeader = new byte[baseSize+hopsSize];
		   
		   sourceRouteHeader[0] = nextHeader;
		   sourceRouteHeader[1] = (byte) hdr_ext_len;
		   sourceRouteHeader[2] = RoutingType;
		   sourceRouteHeader[3] = segmentsLeft;
		   sourceRouteHeader[4] = elidedBytes;
		   sourceRouteHeader[5] = padReserved0;
		   sourceRouteHeader[6] = reserved1;
		   sourceRouteHeader[7] = reserved2;
		   //NOTE: IF CMPRE OR CMPRI != 8 THE FOLLOWING WILL FAIL.
		   int index = 8;
		   for(int i = 2 ;i< path.size();i++){
			   path.get(i);
			   if (path.get(i).getId().length() == 16) {
					// try to parse address
					byte[] nextAddr = DatatypeConverter.parseHexBinary(path.get(i).getId());
					
					for(int j=0;j<nextAddr.length;j++){
						sourceRouteHeader[index] = nextAddr[j];
						index++;
					}
				} else {
					System.out.println("SOURCE ROUTING HEADER: ERROR IN ADDR LENGTH " + path.get(i).getId());
				}
			   
		   }
		   
		   headerBytes= sourceRouteHeader;
		   printRaw(headerBytes);
		
		   /*8-bit unsigned integer.  Number of route segments
                       remaining, i.e., number of explicitly listed
                       intermediate nodes still to be visited before
                       reaching the final destination.  The originator
                       of an SRH sets this field to n, the number of
                       addresses contained in Addresses[1..n].*/
		
		 //  byte cmprI = cmprI<<4;
	}
	
	/**
	 * 
	 * @return the source routing header with addresses appended to the end
	 */
	public byte[] getHeaderBytes(){
		return headerBytes;
	}
	
	private List<Node> dijkstraPathDiscovery(NetworkModel _model, String dest64, NetworkGraph sourceRouteGraph) {
		//retrieve the stable graph
		MultiGraph dijkstraGraph = sourceRouteGraph.getLogicalGraph();

		 // Edge lengths are stored in an attribute called "length"
		 // The length of a path is the sum of the lengths of its edges
		 // The algorithm will store its results in attribute called "result"
		 Dijkstra dijkstra = new Dijkstra(Dijkstra.Element.EDGE, "result", "length");
		
	        
		 // Compute the shortest paths to all nodes from the root mote
		 dijkstra.init(dijkstraGraph);
		 dijkstra.setSource(dijkstraGraph.getNode(_model.getRootMote().getID64()));
		 dijkstra.compute();
		 Path p = dijkstra.getPath(dijkstraGraph.getNode(dest64));
		List<Node> path =  p.getNodePath();
		
		
		System.out.println("path start");
		for(int i = 0 ; i< path.size();i++){
			System.out.println(path.get(i).getId());
		}
		System.out.println("path end");
		 //TODO: complete 
		
		return path;
		
	}

	
	
	
	
	
	public void printRaw(byte[] message){
		for(int i = 0 ; i < message.length;i++){
			if(i>0 && i%4==0.0)
			{
				System.out.println();
			}
			System.out.print(byteToString(message[i]) + " "  );
			//every 4 bytes new line
			
		}
		System.out.println();
	}

	public static String byteToString(byte b){
		return String.format("%8s", Integer.toBinaryString(b&0xFF)).replace(' ', '0');
	}

	public byte[] getFirstHopBytes() {
		return this.nextHop;
	}
}



/*     0                   1                   2                   3
      0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
     +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     |  Next Header  |  Hdr Ext Len  | Routing Type  | Segments Left |
     +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     | CmprI | CmprE |  Pad  |               Reserved                |
     +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     |                                                               |
     .                                                               .
     .                        Addresses[1..n]                        .
     .                                                               .
     |                                                               |
     +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     
     see how function is handled in github line 505 for parsing address, the only supported CmprI/E are ones which result in addresses
     of size 2, 8, and 16 bytes. 
     
     *https://github.com/openwsn-berkeley/openwsn-fw/blob/14240ba35669393faf634c6bd8cbc04f8a5d5bb9/openstack/03b-IPv6/forwarding.c
     *
     *
     *Source routing
     *http://tools.ietf.org/html/rfc6554#page-9
     *
     *
     *
     *Next Header         8-bit selector.  Identifies the type of header
                       immediately following the Routing header.  Uses
                       the same values as the IPv6 Next Header field
                       [RFC2460].

   Hdr Ext Len         8-bit unsigned integer.  Length of the Routing
                       header in 8-octet units, not including the first
                       8 octets.  Note that when Addresses[1..n] are
                       compressed (i.e., value of CmprI or CmprE is not
                       0), Hdr Ext Len does not equal twice the number
                       of Addresses.





Hui, et al.                  Standards Track                    [Page 6]
 
RFC 6554                 RPL Source Route Header              March 2012


   Routing Type        8-bit selector.  Identifies the particular
                       Routing header variant.  An SRH should set the
                       Routing Type to 3.

   Segments Left       8-bit unsigned integer.  Number of route segments
                       remaining, i.e., number of explicitly listed
                       intermediate nodes still to be visited before
                       reaching the final destination.  The originator
                       of an SRH sets this field to n, the number of
                       addresses contained in Addresses[1..n].

   CmprI               4-bit unsigned integer.  Number of prefix octets
                       from each segment, except than the last segment,
                       (i.e., segments 1 through n-1) that are elided.
                       For example, an SRH carrying full IPv6 addresses
                       in Addresses[1..n-1] sets CmprI to 0.

   CmprE               4-bit unsigned integer.  Number of prefix octets
                       from the last segment (i.e., segment n) that are
                       elided.  For example, an SRH carrying a full IPv6
                       address in Addresses[n] sets CmprE to 0.

   Pad                 4-bit unsigned integer.  Number of octets that
                       are used for padding after Address[n] at the end
                       of the SRH.

   Reserved            This field is unused.  It MUST be initialized to
                       zero by the sender and MUST be ignored by the
                       receiver.

   Address[1..n]       Vector of addresses, numbered 1 to n.  Each
                       vector element in [1..n-1] has size (16 - CmprI)
                       and element [n] has size (16-CmprE).  The
                       originator of an SRH places the next (first)
                       hop's IPv6 address in the IPv6 header's IPv6
                       Destination Address and the second hop's IPv6
                       address as the first address in Address[1..n]
                       (i.e., Address[1]).
     *
     */
