/*

 Designer Systems DS-RGBW Shield Arduino Demonstrator
 
 Requires Arduino Duemilanove or MEGA or NANO boards
 
 DS-RGBW.S [A0 & A1 jumpers ON] connected to I2C interface (ANALOG IN 4 [SDA] & 5 [SCL])
 
 RGBW_Application.ino	Date: 24/6/13	Version: 1.00 

 */

#include <Wire.h>

#define SCX18 0x74					// SCX18 I2C Address

// Setup code
void setup()
{
  Wire.begin();						// Start I2C comms
  Serial.begin(250000);

  configureDefault(0,true);
  configureDefault(1,true);
  configureDefault(2,true);
  configureDefault(3,true);
  
  pinMode(A0, OUTPUT);
  digitalWrite(A0, LOW);
  pinMode(A1, INPUT);

}

// Main code
void loop()
{

  int sensorValue = analogRead(A1);
  float f = (1.0f*sensorValue)/1024;

  Serial.println(sensorValue, DEC);
  setPositionInt(0,sensorValue,1024);
  setPositionInt(1,sensorValue,1024);
  setPositionInt(2,sensorValue,1024);
  setPositionInt(3,sensorValue,1024);
  trigger();
  //delay(30);
  
  
  // Channel 1

   /* 
    WriteRegister(1, 34);
    WriteRegister(2, 128);
    WriteRegister(37, 0x00);
    delay(10000);
    /**/

// [34,216]

  
  for(float i=0; i<=f; i+=0.01)
  {
    //Serial.println(i, DEC);
    setAllPositionFloat(i);

    trigger();
    
    delay(10);
  }


  for(float i=f; i>=0; i-=0.01)
  {
    //Serial.println(i, DEC);
    setAllPositionFloat(i);
    trigger();
    
    delay(10);
  }/**/

/*
  setPosition(1,0);
  delay(200);
  setPosition(1,1);
  delay(200);/**/
  
}


void configureDefault(byte pIndex, bool pOn) 
{
  configure(pIndex, true, false, false, 0);
}

void configure(byte pIndex, bool pOn, bool pSoftStart, bool pSpeedControl, byte pSpeed) 
{
  writeRegister(2*pIndex+2,(pOn?128:0)+(pSoftStart?64:0)+(pSpeedControl?16:0)+(pSpeed&15));
  writeRegister(37, 0x00);
}

void setPositionFloat(byte pIndex, float pPos) 
{
  writeRegister(2*pIndex+1,(int)(pPos*(180-34)+34));
}

void setAllPositionFloat(float pPos) 
{
  int value = (int)(pPos*(180-34)+34);
  writeRegister(1,value);
  writeRegister(3,value);
  writeRegister(5,value);
  writeRegister(7,value);
}

void setPositionInt(byte pIndex, int pNum, int pDenom) 
{
  int value = (int)((pNum*(180L-34L))/pDenom+34L);
  //Serial.print("value=");
  //Serial.println(value, DEC);
  writeRegister(2*pIndex+1,value);
}

void trigger() 
{
  writeRegister(37, 0x00);
}

void writeRegister(byte Register, byte Value) 
{
  Wire.beginTransmission(SCX18);
  Wire.write(Register);				      
  Wire.write(Value);
  Wire.endTransmission();				      
}


