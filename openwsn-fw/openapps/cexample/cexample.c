/**
\brief An example CoAP application.
*/

#include "opendefs.h"
#include "cexample.h"
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
#define CEXAMPLEPERIOD  2000
#define PAYLOADLEN      46

const uint8_t cexample_path0[] = "ex";

//=========================== variables =======================================

cexample_vars_t cexample_vars;

//=========================== prototypes ======================================

owerror_t cexample_receive(OpenQueueEntry_t* msg,
                    coap_header_iht*  coap_header,
                    coap_option_iht*  coap_options);
void    cexample_timer_cb(opentimer_id_t id);
void    cexample_task_cb(void);
void    cexample_sendDone(OpenQueueEntry_t* msg,
                       owerror_t error);

//=========================== public ==========================================

void cexample_init() {
   
   // prepare the resource descriptor for the /ex path
   cexample_vars.desc.path0len             = sizeof(cexample_path0)-1;
   cexample_vars.desc.path0val             = (uint8_t*)(&cexample_path0);
   cexample_vars.desc.path1len             = 0;
   cexample_vars.desc.path1val             = NULL;
   cexample_vars.desc.componentID          = COMPONENT_CEXAMPLE;
   cexample_vars.desc.callbackRx           = &cexample_receive;
   cexample_vars.desc.callbackSendDone     = &cexample_sendDone;
   
   
   opencoap_register(&cexample_vars.desc);
   cexample_vars.timerId    = opentimers_start(CEXAMPLEPERIOD,
                                                TIMER_PERIODIC,TIME_MS,
                                                cexample_timer_cb);
}

//=========================== private =========================================

owerror_t cexample_receive(OpenQueueEntry_t* msg,
                      coap_header_iht* coap_header,
                      coap_option_iht* coap_options) {
   return E_FAIL;
}

//timer fired, but we don't want to execute task in ISR mode
//instead, push task to scheduler with COAP priority, and let scheduler take care of it
void cexample_timer_cb(opentimer_id_t id){
   scheduler_push_task(cexample_task_cb,TASKPRIO_COAP);
}

void cexample_task_cb() {
   OpenQueueEntry_t*    pkt;
   owerror_t            outcome;
   uint8_t              i;
   
   // don't run if not synch
   if (ieee154e_isSynch() == FALSE) return;
   
   // don't run on dagroot
   if (idmanager_getIsDAGroot()) {
      opentimers_stop(cexample_vars.timerId);
      return;
   }
   
   
   // create a CoAP RD packet
   pkt = openqueue_getFreePacketBuffer(COMPONENT_CEXAMPLE);
   if (pkt==NULL) {
      openserial_printError(
         COMPONENT_CEXAMPLE,
         ERR_NO_FREE_PACKET_BUFFER,
         (errorparameter_t)0,
         (errorparameter_t)0
      );
      openqueue_freePacketBuffer(pkt);
      return;
   }
   // take ownership over that packet
   pkt->creator                   = COMPONENT_CEXAMPLE;
   pkt->owner                     = COMPONENT_CEXAMPLE;
   // CoAP payload
   packetfunctions_reserveHeaderSize(pkt,PAYLOADLEN);

   //construct packet
   debugNeighborEntry_t* entry =  neighbors_table_entry();
   int index = 0;
   pkt->payload[index++] = 255;
   pkt->payload[index++] = 'n';
   //row
   pkt->payload[index++] = entry->row;//0
   //used
   pkt->payload[index++] = entry->neighborEntry.used;	//1
   //parentRef
   pkt->payload[index++] = entry->neighborEntry.parentPreference;//2
   //stableNeighbor
   pkt->payload[index++] = entry->neighborEntry.stableNeighbor;//3
   //switchStability
   pkt->payload[index++] = entry->neighborEntry.switchStabilityCounter; //4
   //address type
   pkt->payload[index++] = entry->neighborEntry.addr_64b.type;//5
   // the address itself

  /* switch(entry->neighborEntry.addr_64b.type){
    case 1  :
        pkt->payload[index] = entry->neighborEntry.addr_64b.addr_16b; // 6 
	index = index+2;
       break;
    case 2  :*/

	open_addr_t addr =entry->neighborEntry.addr_64b;
	memcpy(&pkt->payload[index],&addr.addr_64b,8);
	//pkt->payload[index] = addr_64b; // 6
	index = index+8;
    /*   break;
    case 3  :
        pkt->payload[index] = entry->neighborEntry.addr_64b.addr_128b; // 6 
	index = index+16;
       break;
    case 4  :
	pkt->payload[index] = entry->neighborEntry.addr_64b.panid;
        index = index+2;
       break;
    case 5 :
	pkt->payload[index] = entry->neighborEntry.addr_64b.prefix;
	index = index+8;
       break;
   }*/
   //dagrank
   pkt->payload[index++] = entry->neighborEntry.DAGrank;
   //rssi
   pkt->payload[index] = entry->neighborEntry.rssi;
   index = index+2;
   //numRx
   pkt->payload[index++] = entry->neighborEntry.numRx;
   //numTx
   pkt->payload[index++] = entry->neighborEntry.numTx;
   //numTxACK
   pkt->payload[index++] = entry->neighborEntry.numTxACK;
   //numWraps
   pkt->payload[index++] = entry->neighborEntry.numWraps;
   //asn
   pkt->payload[index++] = entry->neighborEntry.asn.byte4;
   pkt->payload[index] = entry->neighborEntry.asn.bytes2and3;
   index = index+2;
   pkt->payload[index] = entry->neighborEntry.asn.bytes0and1;
   index = index+2;
   //joinPrio
   pkt->payload[index] = entry->neighborEntry.joinPrio;
   free(entry);
   // metadata
   pkt->l4_destination_port       = WKP_UDP_COAP;
   pkt->l3_destinationAdd.type    = ADDR_128B;
   char addr128[] ={0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
		    0x14,0x15,0x92,0x00,0x00,0x16,0xc0,0x76};
//why does this work????????????????????//
   uint8_t*  DODAGID = get_icmpv6rpl_vars()->dio.DODAGID;
// memcpy(&pkt->l3_destinationAdd.addr_128b[0],&addr128,sizeof(addr128));
   memcpy(&pkt->l3_destinationAdd.addr_128b[0],DODAGID,sizeof(addr128));
   
   // send
   outcome = opencoap_send(
      pkt,
      COAP_TYPE_NON,
      COAP_CODE_RESP_CONTENT,
      1,
      &cexample_vars.desc
   );
   
   // avoid overflowing the queue if fails
   if (outcome==E_FAIL) {
      openqueue_freePacketBuffer(pkt);
   }
   
   return;
}




void cexample_sendDone(OpenQueueEntry_t* msg, owerror_t error) {
   openqueue_freePacketBuffer(msg);
}
