package serial;

public class SerialConstants {
			int a  = 0x0b;
			public static final String[] ERRORS = {"",
					"ERR_RCVD_ECHO_REQUEST " ,
					"ERR_RCVD_ECHO_REPLY " ,
					"ERR_GETDATA_ASKS_TOO_FEW_BYTES " ,
					"ERR_INPUT_BUFFER_OVERFLOW " ,
					"ERR_COMMAND_NOT_ALLOWED " ,
					// l4
					"ERR_WRONG_TRAN_PROTOCOL " ,
					"ERR_WRONG_TCP_STATE " ,
					"ERR_TCP_RESET " ,
					"ERR_UNSUPPORTED_PORT_NUMBER " ,
					// l3
					"ERR_UNEXPECTED_DAO " ,
					"ERR_UNSUPPORTED_ICMPV6_TYPE " ,
					"ERR_6LOWPAN_UNSUPPORTED " ,
					"ERR_NO_NEXTHOP " ,
					"ERR_INVALID_PARAM " ,
					"ERR_INVALID_FWDMODE " ,
					"ERR_LARGE_DAGRANK " ,
					"ERR_HOP_LIMIT_REACHED " ,
					"ERR_LOOP_DETECTED " ,
					"ERR_WRONG_DIRECTION " ,
					// l2b
					"ERR_NEIGHBORS_FULL " ,
					"ERR_NO_SENT_PACKET " ,
					"ERR_NO_RECEIVED_PACKET " ,
					"ERR_SCHEDULE_OVERFLOWN " ,
					// l2a
					"ERR_WRONG_CELLTYPE " ,
					"ERR_IEEE154_UNSUPPORTED " ,
					"ERR_DESYNCHRONIZED " ,
					"ERR_SYNCHRONIZED " ,
					"ERR_LARGE_TIMECORRECTION " ,
					"ERR_WRONG_STATE_IN_ENDFRAME_SYNC " ,
					
					"SUCCESS_NETWORK_STARTED",
					//"ERR_WRONG_STATE_IN_STARTSLOT " ,
					"ERR_WRONG_STATE_IN_TIMERFIRES " ,
					"ERR_WRONG_STATE_IN_NEWSLOT " ,
					"ERR_WRONG_STATE_IN_ENDOFFRAME " ,
					"ERR_MAXTXDATAPREPARE_OVERFLOW " ,
					"ERR_MAXRXACKPREPARE_OVERFLOWS " ,
					"ERR_MAXRXDATAPREPARE_OVERFLOWS " ,
					"ERR_MAXTXACKPREPARE_OVERFLOWS " ,
					"ERR_WDDATADURATION_OVERFLOWS " ,
					"ERR_WDRADIO_OVERFLOWS " ,
					"ERR_WDRADIOTX_OVERFLOWS " ,
					"ERR_WDACKDURATION_OVERFLOWS " ,
					// general
					"ERR_BUSY_SENDING " ,
					"ERR_UNEXPECTED_SENDDONE " ,
					"ERR_NO_FREE_PACKET_BUFFER " ,
					"ERR_FREEING_UNUSED " ,
					"ERR_FREEING_ERROR " ,
					"ERR_UNSUPPORTED_COMMAND " ,
					"ERR_MSG_UNKNOWN_TYPE " ,
					"ERR_WRONG_ADDR_TYPE " ,
					"ERR_BRIDGE_MISMATCH " ,
					"ERR_HEADER_TOO_LONG " ,
					"ERR_INPUTBUFFER_LENGTH " ,
					"ERR_BOOTED " ,
					"ERR_INVALIDSERIALFRAME " ,
					"ERR_INVALIDPACKETFROMRADIO " ,
					"ERR_BUSY_RECEIVING " ,
					"ERR_WRONG_CRC_INPUT " 
					};
			/*
			
			// l3
			"ERR_UNEXPECTED_DAO " = 0x0a, // unexpected DAO (code location {0})
			"ERR_UNSUPPORTED_ICMPV6_TYPE " = 0x0b, // unsupported ICMPv6 type {0} (code location {1})
			"ERR_6LOWPAN_UNSUPPORTED " = 0x0c, // unsupported 6LoWPAN parameter {1} at location {0}
			"ERR_NO_NEXTHOP " = 0x0d, // no next hop
			"ERR_INVALID_PARAM " = 0x0e, // invalid parameter
			"ERR_INVALID_FWDMODE " = 0x0f, // invalid forward mode
			"ERR_LARGE_DAGRANK " = 0x10, // large DAGrank {0}, set to {1}
			"ERR_HOP_LIMIT_REACHED " = 0x11, // packet discarded hop limit reached
			"ERR_LOOP_DETECTED " = 0x12, // loop detected due to previous rank {0} lower than current node rank {1}
			"ERR_WRONG_DIRECTION " = 0x13, // upstream packet set to be downstream, possible loop.
			// l2b
			"ERR_NEIGHBORS_FULL " = 0x14, // neighbors table is full (max number of neighbor is {0})
			"ERR_NO_SENT_PACKET " = 0x15, // there is no sent packet in queue
			"ERR_NO_RECEIVED_PACKET " = 0x16, // there is no received packet in queue
			"ERR_SCHEDULE_OVERFLOWN " = 0x17, // schedule overflown
			// l2a
			"ERR_WRONG_CELLTYPE " = 0x18, // wrong celltype {0} at slotOffset {1}
			"ERR_IEEE154_UNSUPPORTED " = 0x19, // unsupported IEEE802.15.4 parameter {1} at location {0}
			"ERR_DESYNCHRONIZED " = 0x1a, // got desynchronized at slotOffset {0}
			"ERR_SYNCHRONIZED " = 0x1b, // synchronized at slotOffset {0}
			"ERR_LARGE_TIMECORRECTION " = 0x1c, // large timeCorr.: {0} ticks (code loc. {1})
			"ERR_WRONG_STATE_IN_ENDFRAME_SYNC " = 0x1d, // wrong state {0} in end of frame+sync
			"ERR_WRONG_STATE_IN_STARTSLOT " = 0x1e, // wrong state {0} in startSlot, at slotOffset {1}
			"ERR_WRONG_STATE_IN_TIMERFIRES " = 0x1f, // wrong state {0} in timer fires, at slotOffset {1}
			"ERR_WRONG_STATE_IN_NEWSLOT " = 0x20, // wrong state {0} in start of frame, at slotOffset {1}
			"ERR_WRONG_STATE_IN_ENDOFFRAME " = 0x21, // wrong state {0} in end of frame, at slotOffset {1}
			"ERR_MAXTXDATAPREPARE_OVERFLOW " = 0x22, // maxTxDataPrepare overflows while at state {0} in slotOffset {1}
			"ERR_MAXRXACKPREPARE_OVERFLOWS " = 0x23, // maxRxAckPrepapare overflows while at state {0} in slotOffset {1}
			"ERR_MAXRXDATAPREPARE_OVERFLOWS " = 0x24, // maxRxDataPrepapre overflows while at state {0} in slotOffset {1}
			"ERR_MAXTXACKPREPARE_OVERFLOWS " = 0x25, // maxTxAckPrepapre overflows while at state {0} in slotOffset {1}
			"ERR_WDDATADURATION_OVERFLOWS " = 0x26, // wdDataDuration overflows while at state {0} in slotOffset {1}
			"ERR_WDRADIO_OVERFLOWS " = 0x27, // wdRadio overflows while at state {0} in slotOffset {1}
			"ERR_WDRADIOTX_OVERFLOWS " = 0x28, // wdRadioTx overflows while at state {0} in slotOffset {1}
			"ERR_WDACKDURATION_OVERFLOWS " = 0x29, // wdAckDuration overflows while at state {0} in slotOffset {1}
			// general
			"ERR_BUSY_SENDING " = 0x2a, // busy sending
			"ERR_UNEXPECTED_SENDDONE " = 0x2b, // sendDone for packet I didn't send
			"ERR_NO_FREE_PACKET_BUFFER " = 0x2c, // no free packet buffer (code location {0})
			"ERR_FREEING_UNUSED " = 0x2d, // freeing unused memory
			"ERR_FREEING_"ERROR " = 0x2e, // freeing memory unsupported memory
			"ERR_UNSUPPORTED_COMMAND " = 0x2f, // unsupported command {0}
			"ERR_MSG_UNKNOWN_TYPE " = 0x30, // unknown message type {0}
			"ERR_WRONG_ADDR_TYPE " = 0x31, // wrong address type {0} (code location {1})
			"ERR_BRIDGE_MISMATCH " = 0x32, // bridge mismatch (code location {0})
			"ERR_HEADER_TOO_LONG " = 0x33, // header too long, length {1} (code location {0})
			"ERR_INPUTBUFFER_LENGTH " = 0x34, // input length problem, length" ={0}
			"ERR_BOOTED " = 0x35, // booted
			"ERR_INVALIDSERIALFRAME " = 0x36, // invalid serial frame
			"ERR_INVALIDPACKETFROMRADIO " = 0x37, // invalid packet frome radio, length {1} (code location {0})
			"ERR_BUSY_RECEIVING " = 0x38, // busy receiving when stop of serial activity, buffer input length {1} (code location {0})
			"ERR_WRONG_CRC_INPUT " = 0x39, // wrong CRC in input Buffer (input length {0})
			*/
}
