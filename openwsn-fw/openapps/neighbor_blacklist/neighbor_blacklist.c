/**
\brief Receives messages from manager to toggle the connection to a neighbor on/off
*/

#include "opendefs.h"
#include "neighbor_blacklist.h"
#include "opencoap.h"
#include "opentimers.h"
#include "openqueue.h"
#include "packetfunctions.h"
#include "openserial.h"
#include "openrandom.h"
#include "scheduler.h"
#include "neighbors.h"
#include "idmanager.h"
#include "IEEE802154E.h"

//=========================== defines =========================================

const uint8_t neighbor_blacklist_path0[] = "b";
#define PAYLOADLEN      46


//=========================== variables =======================================

blacklist_vars_t blacklist_vars;

//=========================== prototypes ======================================

owerror_t     neighbor_blacklist_receive(
   OpenQueueEntry_t* msg,
   coap_header_iht*  coap_header,
   coap_option_iht*  coap_options
);
void          neighbor_blacklist_sendDone(
   OpenQueueEntry_t* msg,
   owerror_t error
);

//=========================== public ==========================================

/**
\brief Initialize this module.
*/
void neighbor_blacklist_init() {
   
   // prepare the resource descriptor for the /i path
   blacklist_vars.desc.path0len             = sizeof(neighbor_blacklist_path0)-1;
   blacklist_vars.desc.path0val             = (uint8_t*)(&neighbor_blacklist_path0);
   blacklist_vars.desc.path1len             = 0;
   blacklist_vars.desc.path1val             = NULL;
   blacklist_vars.desc.componentID          = COMPONENT_NEIGHBOR_BLACKLIST;
   blacklist_vars.desc.callbackRx           = &neighbor_blacklist_receive;
   blacklist_vars.desc.callbackSendDone     = &neighbor_blacklist_sendDone;
   
   // register with the CoAP module
   opencoap_register(&blacklist_vars.desc);
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
owerror_t neighbor_blacklist_receive(
      OpenQueueEntry_t* msg,
      coap_header_iht* coap_header,
      coap_option_iht* coap_options
   ) {
   
	owerror_t outcome= E_SUCCESS;
	
	open_addr_t address;
	uint8_t i;
	for (i=0; i<8; i++)
	{
		address.addr_64b[i] = msg->l4_payload[1+i];
	}
	neighbors_blacklist_toggle(&address);
	
   return outcome;
}

/**
\brief The stack indicates that the packet was sent.

\param[in] msg The CoAP message just sent.
\param[in] error The outcome of sending it.
*/
void neighbor_blacklist_sendDone(OpenQueueEntry_t* msg, owerror_t error) {
   openqueue_freePacketBuffer(msg);
}