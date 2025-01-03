#include "contiki.h"
#include "coap-engine.h"
#include "dev/leds.h"
#include "os/dev/button-hal.h"
#include "util.h"

#include <string.h>

#include "sys/log.h"
#define LOG_MODULE "App"
#define LOG_LEVEL LOG_LEVEL_APP

static bool actuator_needed = false;
static bool actuator_on = false;
static bool manual = false;
static bool green = false;
static void res_put_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
static void trigger();

EVENT_RESOURCE(light_resource,
         "title=\"light\";rt=\"Text\"", //DA SISTEMARE
         NULL,
         NULL,
         res_put_handler,
         NULL,
         trigger);

static void res_put_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){
    size_t len = 0;
    const char *threshold = NULL;
    const char *action = NULL;
    bool success = true;
    const uint8_t *chunk;

	len=coap_get_payload(request, &chunk);

     if(len>0){
        action = get_json_value_string((char *)chunk, "action");
        //LOG_INFO("received command: action=%s\n", action);
        threshold = get_json_value_string((char *)chunk, "threshold");
        //LOG_INFO("received command: threshold=%s\n", threshold);
	}

    if(threshold!=NULL && strlen(threshold)!=0) {

        if(atoi(threshold)==1){
            actuator_needed=true;
        }
        else{
            actuator_needed=false;
        }

        if(actuator_needed){ //too light or too dark, need to activate the actuator
            //led
            leds_on(LEDS_GREEN);
            leds_on(LEDS_RED);
            green=false;
            if(!actuator_on){
                actuator_on=true;
            }
            manual = false;
        }
        else{ //the light is now in the optimal interval
            if(action!=NULL && strlen(action)>0){
                if(strcmp(action, "true")==0 && !actuator_on){ //the actuator is off and the user wants to turn it on
                    //coap, led and log
                    LOG_INFO("smart light on for user input\n");
                    coap_set_status_code(response,CHANGED_2_04);
                    leds_on(LEDS_GREEN);
                    green=true;
                    actuator_on=true;
                }
                else if(strcmp(action, "false")==0 && actuator_on){ //the actuator is on and the user wants to turn it off
                    //coap
                    LOG_INFO("smart light off for user input\n");
                    coap_set_status_code(response,CHANGED_2_04);
                    if(!green){
                    //if((leds_get() && LEDS_RED)>0){ //automatic deactivation
                        leds_off(LEDS_RED);
                        leds_on(LEDS_GREEN);
                        green=true;
                    }
                    else{ //manual deactivation
                        leds_off(LEDS_GREEN);
                        green=false;
                    }
                    actuator_on=false;
                }
                else{ //wrong input
                    coap_set_status_code(response,BAD_OPTION_4_02);
                }
            }
        }
        success=true;
    }
    else{
        success=false;
    }

    if(!success){
        coap_set_status_code(response, BAD_REQUEST_4_00);
    }
}

static void trigger(){
    if(green){
    //if((leds_get() && LEDS_GREEN)>0){
        leds_off(LEDS_GREEN);
        green=false;
    }
    else{
        LOG_INFO("LED already off \n");
    }
}