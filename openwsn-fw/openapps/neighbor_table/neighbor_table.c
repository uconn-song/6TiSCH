/**
\brief A CoAP resource which indicates the board its running on.
*/

#include "opendefs.h"
#include "neighbor_table.h"
#include "opencoap.h"
#include "opentimers.h"
#include "openqueue.h"
#include "packetfunctions.h"
#include "openserial.h"
#include "openrandom.h"
#include "scheduler.h"
#include "neighbors.h"
//#include "ADC_Channel.h"
#include "idmanager.h"
#include "IEEE802154E.h"

//=========================== defines =========================================

const uint8_t neighbor_table_path0[] = "n";
#define PAYLOADLEN      46


//=========================== variables =======================================

neighbor_table_vars_t neighbor_table_vars;

//=========================== prototypes ======================================

owerror_t     neighbor_table_receive(
   OpenQueueEntry_t* msg,
   coap_header_iht*  coap_header,
   coap_option_iht*  coap_options
);
void          neighbor_table_sendDone(
   OpenQueueEntry_t* msg,
   owerror_t error
);

//=========================== public ==========================================

/**
\brief Initialize this module.
*/
void neighbor_table_init() {
   // do not run if DAGroot
   if(idmanager_getIsDAGroot()==TRUE) return; 
   
   // prepare the resource descriptor for the /i path
   neighbor_table_vars.desc.path0len             = sizeof(neighbor_table_path0)-1;
   neighbor_table_vars.desc.path0val             = (uint8_t*)(&neighbor_table_path0);
   neighbor_table_vars.desc.path1len             = 0;
   neighbor_table_vars.desc.path1val             = NULL;
   neighbor_table_vars.desc.componentID          = COMPONENT_NEIGHBOR_TABLE;
   neighbor_table_vars.desc.callbackRx           = &neighbor_table_receive;
   neighbor_table_vars.desc.callbackSendDone     = &neighbor_table_sendDone;
   
   // register with the CoAP module
   opencoap_register(&neighbor_table_vars.desc);
}

//=========================== private =========================================

/**
\brief Called when a CoAP message is received for this resource.

\param[in] msg          The received message. CoAP header and options already
   parsed.
\param[in] coap_header  The CoAP header contained in the message.
\param[in] coap_options The CoAP options contained in the message.

\return Whether the response is prepared successfully.
*/
owerror_t neighbor_table_receive(
      OpenQueueEntry_t* msg,
      coap_header_iht* coap_header,
      coap_option_iht* coap_options
   ) {
   
   owerror_t outcome;
   
   switch (coap_header->Code) {
      case COAP_CODE_REQ_GET:
         
         //=== reset packet payload (we will reuse this packetBuffer)
         msg->payload                     = &(msg->packet[127]);
         msg->length                      = 0;
         
         //=== prepare  CoAP response
         
  
         packetfunctions_reserveHeaderSize(msg,PAYLOADLEN);

   //construct packet
   debugNeighborEntry_t* entry =  neighbors_table_entry();
   //set payload
   msg->payload[0] = COAP_PAYLOAD_MARKER;
msg->payload[1] = 'n';
   int index = 2;
   //row
   msg->payload[index++] = entry->row;//0
   //used
   msg->payload[index++] = entry->neighborEntry.used;	//1
   //parentRef
   msg->payload[index++] = entry->neighborEntry.parentPreference;//2
   //stableNeighbor
   msg->payload[index++] = entry->neighborEntry.stableNeighbor;//3
   //switchStability
   msg->payload[index++] = entry->neighborEntry.switchStabilityCounter; //4
   //address type
   msg->payload[index++] = entry->neighborEntry.addr_64b.type;//5
   // the address itself

   switch(entry->neighborEntry.addr_64b.type){
    case 1  :
        msg->payload[index] = entry->neighborEntry.addr_64b.addr_16b; // 6 
	index = index+2;
       break;
    case 2  :

	memcpy(msg->payload[index],(entry->neighborEntry.addr_64b.addr_64b),8);

	//msg->payload[index+i] = (uint8_t) (entry->neighborEntry.addr_64b.addr_64b)[i]; // 6

	index = index+8;
      break;
    case 3  :
        msg->payload[index] = entry->neighborEntry.addr_64b.addr_128b; // 6 
	index = index+16;
       break;
    case 4  :
	msg->payload[index] = entry->neighborEntry.addr_64b.panid;
        index = index+2;
       break;
    case 5 :
	msg->payload[index] = entry->neighborEntry.addr_64b.prefix;
	index = index+8;
       break;
   }
   //dagrank
   msg->payload[index++] = entry->neighborEntry.DAGrank;
   //rssi
   msg->payload[index] = entry->neighborEntry.rssi;
   index = index+2;
   //numRx
   msg->payload[index++] = entry->neighborEntry.numRx;
   //numTx
   msg->payload[index++] = entry->neighborEntry.numTx;
   //numTxACK
   msg->payload[index++] = entry->neighborEntry.numTxACK;
   //numWraps
   msg->payload[index++] = entry->neighborEntry.numWraps;
   //asn
   msg->payload[index++] = entry->neighborEntry.asn.byte4;
   msg->payload[index] = entry->neighborEntry.asn.bytes2and3;
   index = index+2;
   msg->payload[index] = entry->neighborEntry.asn.bytes0and1;
   index = index+2;
   //joinPrio
   msg->payload[index] = entry->neighborEntry.joinPrio;

   free(entry);
         // set the CoAP header
         coap_header->Code                = COAP_CODE_RESP_CONTENT;
         
         outcome                          = E_SUCCESS;
         break;
      default:
         // return an error message
         outcome = E_FAIL;
   }
   
   return outcome;
}

/**
\brief The stack indicates that the packet was sent.

\param[in] msg The CoAP message just sent.
\param[in] error The outcome of sending it.
*/
void neighbor_table_sendDone(OpenQueueEntry_t* msg, owerror_t error) {
   openqueue_freePacketBuffer(msg);
}
