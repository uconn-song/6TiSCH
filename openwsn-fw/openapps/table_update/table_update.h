#ifndef __TABLE_UPDATE_H
#define __TABLE_UPDATE_H

/**
\addtogroup AppUdp
\{
\addtogroup TABLE_UPDATE
\{
*/
#include "neighbors.h"
#include "opencoap.h"
//=========================== define ==========================================

//=========================== typedef =========================================

typedef struct {
   coap_resource_desc_t desc;
   opentimer_id_t       timerId;
} table_update_vars_t;

//=========================== variables =======================================

//=========================== prototypes ======================================

void table_update_init();//open_addr_t address, char act
void update(debugNeighborEntry_t* entry, uint8_t ind,  char act);
/**
\}
\}
*/

#endif
