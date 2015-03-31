#ifndef __NEIGHBOR_TABLE_H
#define __NEIGHBOR_TABLE_H

/**
\addtogroup AppCoAP
\{
\addtogroup cinfo
\{
*/

#include "opencoap.h"

//=========================== define ==========================================

//=========================== typedef =========================================

//=========================== variables =======================================

typedef struct {
   coap_resource_desc_t desc;
} neighbor_table_vars_t;

//=========================== prototypes ======================================

void neighbor_table_init(void);

/**
\}
\}
*/

#endif
