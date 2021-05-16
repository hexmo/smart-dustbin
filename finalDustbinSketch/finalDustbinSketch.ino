/*
    We are using library made by K. Suwatchai (Mobizt) to upload data to our firestore instance.
    https://github.com/mobizt/Firebase-ESP-Client/blob/main/examples/Firestore/Create_Documents/Create_Documents.ino
*/

#include <NTPClient.h>
#include <Servo.h>
#include <WiFiUdp.h>
#include <ESP8266TrueRandom.h>
#include <string.h>
#include <stdlib.h>


#if defined(ESP32)
#include <WiFi.h>
#elif defined(ESP8266)
#include <ESP8266WiFi.h>
#endif
#include <Firebase_ESP_Client.h>


//defining wifi credentials
#define WIFI_SSID "SohanWifi@ClassicTech"
#define WIFI_PASSWORD "suh@h@0011"

const String DUSTBIN_ID = "aa45zx";

//for servo
const int servoPin = 5; //D1
const int binOpeningAngle = 180;
const int binClosingAngle = 0;


//other setup
bool isLidOpen = false;
bool inProximity = false;
const int proximityRange = 30; //30 as in 30cm
const int dustbinDepth = 25; //25 as in 25cm

//d3(0),d4(2) are not good for input so use them as trigger
//d5(14), d6(12) for echo

//for depth sensor D
const int trigPinD = 0;
const int echoPinD = 14;
// variables for depth sensor
long durationD;
int distanceD;


//for proximity sensor P
const int trigPinP = 2;
const int echoPinP = 12;
// variables for depth sensor
long durationP;
int distanceP;

//servo
Servo servo;


//Fire base / firestore imports and initilizations

// Define the Firebase project host name and API Key
#define FIREBASE_HOST "smart-dustbin-f1d7e.firebaseio.com"
#define API_KEY "AIzaSyBATdU9J6Sgja9urnfPNfLOxoZLVos_2Y4"

//Define the project ID
#define FIREBASE_PROJECT_ID "smart-dustbin-f1d7e"

//Define the user Email and password that alreadey registerd or added in your project.
#define USER_EMAIL "manas@gmail.com"
#define USER_PASSWORD "fohormalai@100"

//Define Firebase Data object
FirebaseData fbdo;

FirebaseAuth auth;
FirebaseConfig config;



//for time
const long utcOffsetInSeconds = 20700; //+ 5 hours 45 minutes
// Define NTP Client to get time
WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, "pool.ntp.org", utcOffsetInSeconds);

//keep track of whether dustbin was recently opened
bool recentlyOpened = false;



void setup() {
  //WIFI setup

  Serial.begin(9600);

  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting to Wi-Fi");
  while (WiFi.status() != WL_CONNECTED)
  {
    Serial.print(".");
    delay(300);
  }
  Serial.println();
  Serial.print("Connected with IP: ");
  Serial.println(WiFi.localIP());
  Serial.println();

  /* Assign the project host and api key (required) */
  config.host = FIREBASE_HOST;
  config.api_key = API_KEY;

  /* Assign the user sign in credentials */
  auth.user.email = USER_EMAIL;
  auth.user.password = USER_PASSWORD;

  Firebase.begin(&config, &auth);
  Firebase.reconnectWiFi(true);

  //SENSOR SETUP
  pinMode(trigPinP, OUTPUT); // Sets the trigPin as an Output
  pinMode(echoPinP, INPUT);
  pinMode(trigPinD, OUTPUT);
  pinMode(echoPinD, INPUT);

  //init servo
  servo.attach(servoPin);
  servo.write(binClosingAngle);
  isLidOpen = false;
  delayMicroseconds(2000);

  recentlyOpened = false;
  //init time
  timeClient.begin();
}


//////other setup
//bool isLidOpen = false;
//bool inProximity = false;


void loop() {

  delay(2500);

  if (isProximity()) {
    servo.write(binOpeningAngle);
    isLidOpen = true;
    recentlyOpened = true;
  } else {
    servo.write(binClosingAngle);
    isLidOpen = false;
  }

  if (recentlyOpened) {
    if (isLidOpen == false) {
      Serial.println("Lid was recently closed.");
      recentlyOpened = false;
      writeDataToFirebase();
    }
  }

}

int getDepth() {

  // Clears the trigPin
  digitalWrite(trigPinD, LOW);
  delayMicroseconds(2);

  // Sets the trigPin on HIGH state for 10 micro seconds
  digitalWrite(trigPinD, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPinD, LOW);

  // Reads the echoPin, returns the sound wave travel time in microseconds
  durationD = pulseIn(echoPinD, HIGH);

  // Calculating the distance
  distanceD = durationD * 0.034 / 2;

  Serial.print("Waste level: ");
  Serial.println(distanceD);


  //Here we calculate the filled dustbin space in percentage
  //converting int to string for firebase.

  return distanceD;
}

//This method returns true when the sensor detects person in the range of sensor and vice versa.
bool isProximity() {
  // Clears the trigPin
  digitalWrite(trigPinP, LOW);
  delayMicroseconds(2);

  // Sets the trigPin on HIGH state for 10 micro seconds
  digitalWrite(trigPinP, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPinP, LOW);

  // Reads the echoPin, returns the sound wave travel time in microseconds
  durationP = pulseIn(echoPinP, HIGH);

  // Calculating the distance
  distanceP = durationP * 0.034 / 2;

  Serial.print("Proximity Depth: ");
  Serial.println(distanceP);

  // Return boolean values on basis of proximity range.
  if (distanceP < proximityRange) {
    return true;
  }

  return false;
}

void writeDataToFirebase() {
  //    Serial.println(getTime());
  String content,content2;
  FirebaseJson js, js2;
  int depth = getDepth();

  Serial.println("Uploading dustbin levels.....");

  // This library is used to generate random id for each document.

  String historyID = String(ESP8266TrueRandom.random());
  //  https://github.com/marvinroger/ESP8266TrueRandom
  String documentPath = "history/" + historyID;

  js.set("fields/dustbinId/stringValue", DUSTBIN_ID);
  js.set("fields/timeStamp/stringValue", getTime());
  js.set("fields/wasteId/stringValue", historyID);
  js.set("fields/wasteLevel/integerValue", depth);


  js.toString(content);



  if (Firebase.Firestore.createDocument(&fbdo, FIREBASE_PROJECT_ID, "" /* databaseId can be (default) or empty */, documentPath.c_str(), content.c_str()))
  {
    Serial.println("Uploaded Data");
    Serial.println(fbdo.payload());
    Serial.println();
  }
  else
  {
    Serial.println("Failed to Upload Data");
    Serial.println("REASON: " + fbdo.errorReason());
    Serial.println();
  }


  String documentPath2 = "dustbins/" + DUSTBIN_ID;
  js2.set("fields/wasteLevel/integerValue", depth);
  js2.toString(content2);

  if (Firebase.Firestore.patchDocument(&fbdo, FIREBASE_PROJECT_ID, "", documentPath2.c_str(), content2.c_str(),"wasteLevel"))
  {
    Serial.println("Uploaded second Data");
    Serial.println(fbdo.payload());
    Serial.println();
  }
  else
  {
    Serial.println("Failed to second Upload Data");
    Serial.println("REASON: " + fbdo.errorReason());
    Serial.println();
  }



}

//Since we do not have on board clock on our LoLin NodeMCU we get date and time from NTP servers.
String getTime() {

  timeClient.update();
  //Code referenced from https://randomnerdtutorials.com/esp8266-nodemcu-date-time-ntp-client-server-arduino/
  unsigned long epochTime = timeClient.getEpochTime();
  String formattedTime = timeClient.getFormattedTime();

  struct tm *ptm = gmtime ((time_t *)&epochTime);
  int monthDay = ptm->tm_mday;
  int currentMonth = ptm->tm_mon + 1;
  int currentYear = ptm->tm_year + 1900;

  String currentDate = String(currentYear) + "-" + String(currentMonth) + "-" + String(monthDay) + " " + formattedTime;
  return currentDate;
}
