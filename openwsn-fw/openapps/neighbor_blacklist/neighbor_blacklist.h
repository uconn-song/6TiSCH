#ifndef __NEIGHBOR_BLACKLIST_H
#define __NEIGHBOR_BLACKLIST_H

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
} blacklist_vars_t;

//=========================== prototypes ======================================

void neighbor_blacklist_init(void);

/**
\}
\}
*/

#endif
