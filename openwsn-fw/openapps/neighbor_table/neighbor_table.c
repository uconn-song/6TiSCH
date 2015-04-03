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
#define SENDPERIOD  1000
int rowIndex = 0;

//=========================== variables =======================================

neighbor_table_vars_t neighbor_table_vars;

//=========================== prototypes ======================================

owerror_t     neighbor_table_receive(
   OpenQueueEntry_t* msg,
   coap_header_iht*  coap_header,
   coap_option_iht*  coap_options
);
void    app_timer_cb(opentimer_id_t id);
void send_next_row(void);
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
   
   // prepare the resource descriptor for the /n path
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
    /*     
   //=== reset packet payload (we will reuse this packetBuffer)
   msg->payload                     = &(msg->packet[127]);
   msg->length                      = 0;
   //=== prepare  CoAP response
   
   packetfunctions_reserveHeaderSize(msg,PAYLOADLEN);

   //construct packet
   debugNeighborEntry_t* entry =  neighbors_table_entry(0);
   //set payload
   msg->payload[0] = COAP_PAYLOAD_MARKER;//1
   msg->payload[1] = 'n';//2
   int index = 2;
   //row
   msg->payload[index++] = entry->row;//3
   //used
   msg->payload[index++] = entry->neighborEntry.used;	//4
   //parentRef
   msg->payload[index++] = entry->neighborEntry.parentPreference;//5
   //stableNeighbor
   msg->payload[index++] = entry->neighborEntry.stableNeighbor;//6
   //switchStability
   msg->payload[index++] = entry->neighborEntry.switchStabilityCounter; //7
   //address type
   msg->payload[index++] = entry->neighborEntry.addr_64b.type;//8
    // the address itself
	open_addr_t addr =entry->neighborEntry.addr_64b;//16
	memcpy(&msg->payload[index],&addr.addr_64b,8);
	index = index+8;
   //dagrank
   msg->payload[index++] = entry->neighborEntry.DAGrank;//17
   //rssi
   msg->payload[index] = entry->neighborEntry.rssi;//19
   index = index+2;
   //numRx
   msg->payload[index++] = entry->neighborEntry.numRx;//20
   //numTx
   msg->payload[index++] = entry->neighborEntry.numTx;//21
   //numTxACK
   msg->payload[index++] = entry->neighborEntry.numTxACK;//22
   //numWraps
   msg->payload[index++] = entry->neighborEntry.numWraps;//23
   //asn
   msg->payload[index++] = entry->neighborEntry.asn.byte4;//24
   msg->payload[index] = entry->neighborEntry.asn.bytes2and3;//26
   index = index+2;
   msg->payload[index] = entry->neighborEntry.asn.bytes0and1;//28
   index = index+2;
   //joinPrio
   msg->payload[index] = entry->neighborEntry.joinPrio;//29

   free(entry);
   // set the CoAP header

   coap_header->Code                = COAP_CODE_RESP_CONTENT;
*/
   outcome                          = E_SUCCESS;

	// start timer to send the rest of the neigbor table

	neighbor_table_vars.timerId = opentimers_start(SENDPERIOD,TIMER_PERIODIC,TIME_MS,
                                                app_timer_cb);
         break;
      default:
         // return an error message
         outcome = E_FAIL;
	}

   return outcome;
}

void app_timer_cb(opentimer_id_t id){
   scheduler_push_task(send_next_row,TASKPRIO_COAP);
}

void send_next_row() {
   OpenQueueEntry_t*    packet;
   owerror_t            outcome;
   uint8_t              i;

	//send the remaining 9 rows and reset the index counter
   if (rowIndex == 10) {
      opentimers_stop(neighbor_table_vars.timerId);
      rowIndex =0;
      return;
   }
   
   // create a CoAP RD packet
   packet = openqueue_getFreePacketBuffer(COMPONENT_NEIGHBOR_TABLE);
   if (packet==NULL) {
      openserial_printError(
         COMPONENT_NEIGHBOR_TABLE,
         ERR_NO_FREE_PACKET_BUFFER,
         (errorparameter_t)0,
         (errorparameter_t)0
      );
      openqueue_freePacketBuffer(packet);
      return;
   }
   // take ownership over that packet
   packet->creator                   = COMPONENT_NEIGHBOR_TABLE;
   packet->owner                     = COMPONENT_NEIGHBOR_TABLE;
   // CoAP payload
   packetfunctions_reserveHeaderSize(packet,PAYLOADLEN);

   //construct packet
   debugNeighborEntry_t* entry =  neighbors_table_entry(rowIndex);
	//increment the index for the next func call
	rowIndex++;
   int index = 2;
   packet->payload[0] = COAP_PAYLOAD_MARKER;
	packet->payload[1] = 'n';
   //row
   packet->payload[index++] = entry->row;//0
   //used
   packet->payload[index++] = entry->neighborEntry.used;	//1
   //parentRef
   packet->payload[index++] = entry->neighborEntry.parentPreference;//2
   //stableNeighbor
   packet->payload[index++] = entry->neighborEntry.stableNeighbor;//3
   //switchStability
   packet->payload[index++] = entry->neighborEntry.switchStabilityCounter; //4
   //address type
   packet->payload[index++] = entry->neighborEntry.addr_64b.type;//5
   // the address itself
	open_addr_t addr =entry->neighborEntry.addr_64b;
	memcpy(&packet->payload[index],&addr.addr_64b,8);
	index = index+8;
   //dagrank
   packet->payload[index++] = entry->neighborEntry.DAGrank;
   //rssi
   packet->payload[index] = entry->neighborEntry.rssi;
   index = index+2;
   //numRx
   packet->payload[index++] = entry->neighborEntry.numRx;
   //numTx
   packet->payload[index++] = entry->neighborEntry.numTx;
   //numTxACK
   packet->payload[index++] = entry->neighborEntry.numTxACK;
   //numWraps
   packet->payload[index++] = entry->neighborEntry.numWraps;
   //asn
   packet->payload[index++] = entry->neighborEntry.asn.byte4;
   packet->payload[index] = entry->neighborEntry.asn.bytes2and3;
   index = index+2;
   packet->payload[index] = entry->neighborEntry.asn.bytes0and1;
   index = index+2;
   //joinPrio
   packet->payload[index] = entry->neighborEntry.joinPrio;
   free(entry);
   // metadata
   packet->l4_destination_port       = WKP_UDP_COAP;
   packet->l3_destinationAdd.type    = ADDR_128B;
   //memcpy(&packet->l3_destinationAdd.addr_128b[0],(*get_icmpv6rpl_vars()).dio.DODAGID,16);
   uint8_t         manager_full_address[16];
	   
	   // retrieve my prefix and EUI64
	memcpy(&manager_full_address[0],idmanager_getMyID(ADDR_PREFIX)->prefix,8); // prefix
	memcpy(&manager_full_address[8],&manager_address,8);  // manager address
   memcpy(&packet->l3_destinationAdd.addr_128b[0],&manager_full_address,16);
   // send
   outcome = opencoap_send(
      packet,
      COAP_TYPE_NON,
      COAP_CODE_RESP_CONTENT,
      1,
      &neighbor_table_vars.desc
   );
   
   // avoid overflowing the queue if fails
   if (outcome==E_FAIL) {
      openqueue_freePacketBuffer(packet);
   }

   return;
}

/**
\brief The stack indicates that the packet was sent.

\param[in] msg The CoAP message just sent.
\param[in] error The outcome of sending it.
*/
void neighbor_table_sendDone(OpenQueueEntry_t* msg, owerror_t error) {
   openqueue_freePacketBuffer(msg);
}
