#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "contiki.h"
#include "contiki-net.h"
#include "coap-engine.h"
#include "os/dev/button-hal.h"
#include "coap-blocking-api.h"
#include "dev/leds.h"

/* Log configuration */
#include "coap-log.h"
#define LOG_MODULE "App"
#define LOG_LEVEL  LOG_LEVEL_APP

#define SERVER_EP "coap://[fd00::1]:5683"
static bool registration_done = false;
char *service_url = "/registrator";
// Define a handler to handle the response from the server
void client_chunk_handler(coap_message_t *response)
{
  const uint8_t *chunk;

  if(response == NULL) {
    puts("Request timed out");
    return;
  }

  int len = coap_get_payload(response, &chunk);

  printf("|%.*s", len, (char *)chunk);
  registration_done=true;
}

extern coap_resource_t light_resource;

PROCESS(smart_light_client, "Smart Light Client");
AUTOSTART_PROCESSES(&smart_light_client);

PROCESS_THREAD(smart_light_client, ev, data){

    static coap_endpoint_t server_ep;
    static coap_message_t request[1];      /* This way the packet can be treated as pointer as usual. */

    PROCESS_BEGIN();

    leds_off(LEDS_RED);
    leds_off(LEDS_GREEN);
    leds_off(LEDS_YELLOW);

    coap_endpoint_parse(SERVER_EP, strlen(SERVER_EP), &server_ep);
    coap_init_message(request, COAP_TYPE_CON, COAP_POST, 0);
    coap_set_header_uri_path(request, service_url);
    const char msg[] = "{\"type\":\"light\"}";
    coap_set_payload(request, (uint8_t *)msg, sizeof(msg) - 1);

    while(!registration_done){
        COAP_BLOCKING_REQUEST(&server_ep, request, client_chunk_handler);
    }
    
    coap_activate_resource(&light_resource, "light");

    button_hal_button_t *btn;
        btn = button_hal_get_by_index(0);
        if(btn){
    	printf("%s \n",
    		BUTTON_HAL_GET_DESCRIPTION(btn));
        }

    while(1){
        PROCESS_WAIT_EVENT();
        //DA DECIDERE PER IL BOTTONE
        if(ev==button_hal_release_event){
            light_resource.trigger();
        }
    }

    PROCESS_END();
}