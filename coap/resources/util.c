#include "json_util.h"

#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <stdint.h>


char* get_json_value_string(const char* jsonString, const char* fieldName){

        char* value=NULL;

        char string_json[50];

        strcpy(string_json,jsonString);

        

        for(int i=0;i<strlen(jsonString);i++){
                string_json[i]=string_json[i+1];
        }

        string_json[strlen(string_json)-1]='\0';


        char* token = strtok(string_json, ",");
        char first_command[20];

        if(strcmp(fieldName,"action")==0){
                strcpy(first_command,token);
        }
        else if(strcmp(fieldName,"threshold")==0){
                while (token != NULL) {
                        strcpy(first_command,token);
                        token = strtok(NULL, ",");
                }
        }


        char* token2 = strtok(first_command, ":");
        char value_action[10];

        while (token2 != NULL) {
                strcpy(value_action,token2);
                token2 = strtok(NULL, ":");
        }

        value = (char*)malloc((strlen(value_action) + 1) * sizeof(char));
        if (value == NULL) {
                printf("Memory allocation failed!\n");
                return NULL;
        }

        strcpy(value,value_action);
        
        return value;
}