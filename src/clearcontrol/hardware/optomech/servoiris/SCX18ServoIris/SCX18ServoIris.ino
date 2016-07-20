/*
  SCX18 Servo Iris Controller

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

  for (int r=0; r<3; r++)
  {
    for(float i=0; i<=512; i++)
    {
      //Serial.println(i, DEC);
      setAllPositionInt(i,512);
  
      trigger();
      
      delay(1);
    }
  
    for(float i=512; i>=0; i--)
    {
      //Serial.println(i, DEC);
      setAllPositionInt(i,512);
      trigger();
      
      delay(1);
    }/**/
  }

}

int lastValue=-100;

// Main code
void loop()
{

  const int sensorValue = analogRead(A1);
  const int value = scaleValue(sensorValue,1024);

  if(abs(value-lastValue)>1)
  {
    setPositionInt(0,value);
    setPositionInt(1,value);
    setPositionInt(2,value);
    setPositionInt(3,value);
    trigger();
    lastValue = value;
    //Serial.println(value, DEC);
    delay(1);
  }

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

void setAllPositionInt(int pNum, int pDenom) 
{
  int value = scaleValue(pNum,pDenom);
  writeRegister(1,value);
  writeRegister(3,value);
  writeRegister(5,value);
  writeRegister(7,value);
}

void setPositionInt(byte pIndex, int pNum, int pDenom) 
{
  int value = scaleValue(pNum,pDenom);
  //Serial.print("value=");
  //Serial.println(value, DEC);
  writeRegister(2*pIndex+1,value);
}

inline int scaleValue(int value, int scale)
{
  return  (int)((value*(180L-34L))/scale+34L);
}

void setPositionInt(byte pIndex, int pVal) 
{
  writeRegister(2*pIndex+1,pVal);
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


        



