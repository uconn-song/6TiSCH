/**
\brief An example CoAP application.
*/

#include "opendefs.h"
#include "table_update.h"
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

/// inter-packet period (in ms)
#define WAITPERIOD  5000
#define PAYLOADLEN      30//46


const uint8_t table_update_path0[] = "up";

//=========================== variables =======================================

table_update_vars_t table_update_vars;
debugNeighborEntry_t* tableEntry;
char action; // either 'r' or 'a' for remove/adding
uint8_t i; // this is the row number from the table where the row was removed
//=========================== prototypes ======================================

owerror_t table_update_receive(OpenQueueEntry_t* msg,
                    coap_header_iht*  coap_header,
                    coap_option_iht*  coap_options);
void    table_update_timer_cb(opentimer_id_t id);
void    table_update_task_cb(void);
void    table_update_sendDone(OpenQueueEntry_t* msg,
                       owerror_t error);

//=========================== public ==========================================

//open_addr_t address, char act
void table_update_init() {
   
   // prepare the resource descriptor for the /up path
   table_update_vars.desc.path0len             = sizeof(table_update_path0)-1;
   table_update_vars.desc.path0val             = (uint8_t*)(&table_update_path0);
   table_update_vars.desc.path1len             = 0;
   table_update_vars.desc.path1val             = NULL;
   table_update_vars.desc.componentID          = COMPONENT_TABLE_UPDATE;
   table_update_vars.desc.callbackRx           = &table_update_receive;
   table_update_vars.desc.callbackSendDone     = &table_update_sendDone;
   opencoap_register(&table_update_vars.desc);
   
}

//=========================== private =========================================

owerror_t table_update_receive(OpenQueueEntry_t* msg,
                      coap_header_iht* coap_header,
                      coap_option_iht* coap_options) {
   return E_FAIL;
}

//timer fired, but we don't want to execute task in ISR mode
//instead, push task to scheduler with COAP priority, and let scheduler take care of it
void table_update_timer_cb(opentimer_id_t id){

   	scheduler_push_task(table_update_task_cb,TASKPRIO_COAP);

}

void update(debugNeighborEntry_t* entry, uint8_t ind, char act)
{
table_update_vars.desc.path0len             = sizeof(table_update_path0)-1;
   table_update_vars.desc.path0val             = (uint8_t*)(&table_update_path0);
   table_update_vars.desc.path1len             = 0;
   table_update_vars.desc.path1val             = NULL;
   table_update_vars.desc.componentID          = COMPONENT_TABLE_UPDATE;
   table_update_vars.desc.callbackRx           = &table_update_receive;
   table_update_vars.desc.callbackSendDone     = &table_update_sendDone;

   opencoap_register(&table_update_vars.desc);
   i = ind;
   action = act; 
tableEntry = entry;
//scheduler_push_task(table_update_task_cb,TASKPRIO_COAP);
  table_update_vars.timerId    = opentimers_start(WAITPERIOD,
                                                TIMER_ONESHOT,TIME_MS,
                                                table_update_timer_cb);
}

void table_update_task_cb() {
   OpenQueueEntry_t*    pkt;
   owerror_t            outcome;

   
   // don't run if not synch
   if (ieee154e_isSynch() == FALSE ) return;
   
   // don't run on dagroot
   if (idmanager_getIsDAGroot()) {
      opentimers_stop(table_update_vars.timerId);
      return;
   }
   
   // create a CoAP RD packet
   pkt = openqueue_getFreePacketBuffer(COMPONENT_TABLE_UPDATE);
   if (pkt==NULL) {
      openserial_printError(
         COMPONENT_TABLE_UPDATE,
         ERR_NO_FREE_PACKET_BUFFER,
         (errorparameter_t)0,
         (errorparameter_t)0
      );
      openqueue_freePacketBuffer(pkt);
      return;
   }
   // take ownership over that packet
   pkt->creator                   = COMPONENT_TABLE_UPDATE;
   pkt->owner                     = COMPONENT_TABLE_UPDATE;
   // CoAP payload
   packetfunctions_reserveHeaderSize(pkt,PAYLOADLEN);

   //construct packet

   int index = 0;
   pkt->payload[index++] = 255;//1
   pkt->payload[index++] = action;//2
   //pkt->payload[index++] = count++;
   pkt->payload[index++] = i;//3
   //used
   pkt->payload[index++] = tableEntry->neighborEntry.used;//4	
   //parentRef
   pkt->payload[index++] = tableEntry->neighborEntry.parentPreference;//5
   //stableNeighbor
   pkt->payload[index++] = tableEntry->neighborEntry.stableNeighbor;//6
   //switchStability
   pkt->payload[index++] = tableEntry->neighborEntry.switchStabilityCounter;//7
   //address type
   pkt->payload[index++] = tableEntry->neighborEntry.addr_64b.type;//8
       // the address itself
open_addr_t addr =tableEntry->neighborEntry.addr_64b;//16
memcpy(&pkt->payload[index],&addr.addr_64b,8);
index = index+8;
   //dagrank
   pkt->payload[index++] = tableEntry->neighborEntry.DAGrank;//17
   //rssi
   pkt->payload[index] = tableEntry->neighborEntry.rssi;//19
   index = index+2;
   //numRx
   pkt->payload[index++] = tableEntry->neighborEntry.numRx;//20
   //numTx
   pkt->payload[index++] = tableEntry->neighborEntry.numTx;//21
   //numTxACK
   pkt->payload[index++] = tableEntry->neighborEntry.numTxACK;//22
   //numWraps
   pkt->payload[index++] = tableEntry->neighborEntry.numWraps;//23
   //asn
   pkt->payload[index++] = tableEntry->neighborEntry.asn.byte4;//24
   pkt->payload[index] = tableEntry->neighborEntry.asn.bytes2and3;//26
   index = index+2;
   pkt->payload[index] = tableEntry->neighborEntry.asn.bytes0and1;//28
   index = index+2;
   //joinPrio
   pkt->payload[index] = tableEntry->neighborEntry.joinPrio;//29
	

   // metadata
   pkt->l4_destination_port       = WKP_UDP_COAP;
   pkt->l3_destinationAdd.type    = ADDR_128B;


   //uint8_t*  DODAGID = get_icmpv6rpl_vars()->dio.DODAGID;
	uint8_t         manager_full_address[16];
	   
	   // retrieve my prefix and EUI64
	memcpy(&manager_full_address[0],idmanager_getMyID(ADDR_PREFIX)->prefix,8); // prefix
	memcpy(&manager_full_address[8],&manager_address,8);  // manager address
   memcpy(&pkt->l3_destinationAdd.addr_128b[0],&manager_full_address,16);

	//free some memory
	free(tableEntry);
   // send
   outcome = opencoap_send(
      pkt,
      COAP_TYPE_NON,
      COAP_CODE_RESP_CONTENT,
      1,
      &table_update_vars.desc
   );
   
   // avoid overflowing the queue if fails
   if (outcome==E_FAIL) {
      openqueue_freePacketBuffer(pkt);
   }
   
   return;
}




void table_update_sendDone(OpenQueueEntry_t* msg, owerror_t error) {
   openqueue_freePacketBuffer(msg);
}
