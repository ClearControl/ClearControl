/*
 Motorized iris controller
 Loic Royer, 2015
 */

#include <Wire.h>

#define SCX18 0x74					// SCX18 I2C Address

// Setup code
void setup()
{
  Wire.begin();						// Start I2C comms
  Serial.begin(250000);
  Serial.setTimeout(10);

  pinMode(A0, INPUT);
  pinMode(A1, INPUT);

  pinMode(8, INPUT);
  pinMode(9, INPUT);
  pinMode(10, INPUT);
  pinMode(11, INPUT);

  configureDefault(1,true);
  configureDefault(2,true);
  configureDefault(3,true);
  configureDefault(4,true);

  configureDefault(8,true);
  configureDefault(9,true);
  configureDefault(10,true);
  configureDefault(11,true);


   for(int i=0; i<20; i++)
    ramp(0.01,50);
  /**/

}

int currentValue = -1000;
int filteredValue = 0;

// Main code
void loop()
{
  do
  {
   int sensorValue = analogRead(A1)-analogRead(A0);
   filteredValue = (filteredValue*7+sensorValue)>>3;
  }
  while(abs(currentValue-filteredValue)<1);
  
  currentValue = filteredValue;
  
  float f = (1.0f*currentValue)/1024;

  //Serial.println(sensorValue, DEC);
  setPositionInt(1,currentValue,1024);
  setPositionInt(2,currentValue,1024);
  setPositionInt(3,currentValue,1024);
  setPositionInt(4,currentValue,1024);
  trigger();
}


void configureDefault(byte pIndex, bool pOn) 
{
  configure(pIndex, true, false, false, 0);
}

void configure(byte pIndex, bool pOn, bool pSoftStart, bool pSpeedControl, byte pSpeed) 
{
  writeRegister(2*pIndex,(pOn?128:0)+(pSoftStart?64:0)+(pSpeedControl?16:0)+(pSpeed&15));
  writeRegister(37, 0x00);
}

void ramp(float pStep, int pSleepTimeIMicroSeconds)
{
  //effective range of servo: [34,216]
  
  for(float i=0; i<=1; i+=pStep)
  {
    setAllPositionFloat(i);
    trigger();
    delayMicroseconds(pSleepTimeIMicroSeconds);
  }
  delay(5);

  for(float i=1; i>=0; i-=pStep)
  {
    setAllPositionFloat(i);
    trigger();
    delayMicroseconds(pSleepTimeIMicroSeconds);
  }/**/
  delay(5);
}

inline void setPositionFloat(byte pIndex, float pPos) 
{
  writeRegister(2*pIndex-1,(int)(pPos*(180-34)+34));
}

inline void setAllPositionFloat(float pPos) 
{
  int value = (int)(pPos*(170-34)+34);
  writeRegister(1,value);
  writeRegister(3,value);
  writeRegister(5,value);
  writeRegister(7,value);
}

inline void setPositionInt(byte pIndex, int pNum, int pDenom) 
{
  int value = (int)((pNum*(170L-34L))/pDenom+34L);
  //Serial.print("value=");
  //Serial.println(value, DEC);
  writeRegister(2*pIndex-1,value);
}

inline void setPosition(byte pIndex, byte pPos) 
{
  writeRegister(2*pIndex-1,pPos);
}

inline void trigger() 
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


