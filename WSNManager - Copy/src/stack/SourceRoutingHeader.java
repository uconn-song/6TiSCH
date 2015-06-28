package stack;

import java.util.ArrayList;

import network_model.NetworkModel;

public class SourceRoutingHeader {
	private byte[] headerBytes;
	public SourceRoutingHeader(NetworkModel _model, String dest64,
			int prefixBytesElided) {
		System.out.println("implement source routing header");
			byte             local_CmprE;
		   byte              local_CmprI;
		   byte             numAddr;
		   byte             hlen;
		   
		   //begin building source routing path
		   ArrayList<String> path = new ArrayList<String>();
		   /*
		    * Build path backwards
		    * possible algorithm
		    * 	start from destination mote and find preferred parent and record
		    * 		verify that path is not blocked by manager, otherwise choose neighbor with lowest
		    *   find preferred parent of that mote and record in arraylist
		    *   repeat until reaching root.
		    */
		   
		   byte nextHeader = 0x11; //udp is the next header
		   System.out.println("TODO: update length of header");
		   byte HdrExtLen = 0; //unknown until we have the path
		   
		   
		  /* 8-bit selector.  Identifies the particular
           Routing header variant.  An SRH should set the
           Routing Type to 3.*/
		   byte RoutingType = 0x03; 
		   System.out.println("TODO:set number segments left source routing");
		   /*8-bit unsigned integer.  Number of route segments
                       remaining, i.e., number of explicitly listed
                       intermediate nodes still to be visited before
                       reaching the final destination.  The originator
                       of an SRH sets this field to n, the number of
                       addresses contained in Addresses[1..n].*/
		   byte segmentsLeft = 0;
		 //  byte cmprI = cmprI<<4;
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
