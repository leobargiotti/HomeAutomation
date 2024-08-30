#include "contiki.h"
#include "coap-engine.h"
#include "dev/leds.h"
#include "os/dev/button-hal.h"
#include "json_util.h"

#include <string.h>

static bool actuator_needed = false;
static bool actuator_on = false;
static void res_put_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);

RESOURCE(res_light,
         "title=\"light\";rt=\"Text\"", //DA SISTEMARE
         NULL,
         NULL,
         res_put_handler,
         NULL);

static void res_put_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){
    size_t len = 0;
    const char *command = NULL;
    uint8_t led = 0;
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

        if(atoi(threshold)==1 || atoi(threshold)==2){
            actuator_needed=true;
        }

        if(actuator_needed){ //too light or too dark, need to activate the actuator
            //led
            leds_on(LEDS_GREEN)
            leds_on(LEDS_RED);
            if(!actuator_on){
                actuator_on=true;
            }
        }
        else{ //the light is now in the optimal interval
            if(action!=NULL && strlen(action)>0){
                if(strcmp(action, "ON_UP")==0 && !actuator_on){ //the actuator is off and the user wants to turn it on to increase light
                    //coap, led and log
                    LOG_INFO("smart light on for user input\n");
                    coap_set_status_code(response,CHANGED_2_04);
                    leds_on(LEDS_GREEN);
                    actuator_on=true;
                }
                else if(strcmp(action, "ON_DOWN")==0 && !actuator_on){ //the actuator is off and the user wants to turn it on to decrease light
                    //coap, led and log
                    LOG_INFO("smart light on for user input\n");
                    coap_set_status_code(response,CHANGED_2_04);
                    leds_on(LEDS_GREEN);
                    actuator_on=true;
                }
                else if(strcmp(action, "OFF")==0 && actuator_on){ //the actuator is on and the user wants to turn it off
                    //coap
                    LOG_INFO("smart light off for user input\n");
                    coap_set_status_code(response,CHANGED_2_04);
                    leds_off(LEDS_GREEN);
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

static void button_pressed(){
    if(!manual){
        manual = true;
        leds_off(LEDS_RED);
        leds_off(LEDS_GREEN);
        leds_on(LEDS_GREEN);
    }
    else{
        if(leds_get()&&LEDS_GREEN){
            leds_off(LEDS_GREEN);
        }
        else{
            leds_on(LEDS_GREEN);
        }
    }
}