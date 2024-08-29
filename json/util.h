#ifndef JSON_UTIL_H
#define JSON_UTIL_H

char* get_json_value_string(const char* jsonString, const char* fieldName);
int get_json_value_integer(const char* jsonString, const char* fieldName);

#endif