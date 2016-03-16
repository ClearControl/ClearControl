//example from http://labs.arduino.org/Arduino+Motor+Shield%3A+Two+DC+Motors+Example

//Define the variables used in the sketch
/*DC_Motor1 connected in Channel A */
int pinDirA = 12;//Variable used to direct the Motor1
int pinBrkA = 9;//Varialble used to brake Motor1
int pinPwmA = 3;//Variable used to select the speed Motor1

/* DC_Motor2 connected in Channel B */
int pinDirB = 13;//Variable used to direct the Motor2
int pinBrkB = 8;//Variable used to brake the Motor2
int pinPwmB = 11;//Variable used to select the speed Motor2

unsigned long pulseTime = 10;
unsigned long relaxTime = 300;

int IO1 = A2; //digital input 1
int IO2 = A3; //digital input 2

byte minCurrent = 40; //This is the min current 
byte maxCurrent = 255; //This is the max current


volatile unsigned long lastUpdateTime1 = 0;
volatile unsigned long lastUpdateTime2 = 0;

void setup() 
{
    
 Serial.begin(9600);    

  pinMode(IO1,INPUT);
  pinMode(IO2,INPUT);
    
  //initialize the Motor1
  pinMode(pinDirA, OUTPUT);
  //pinMode(pinPwmA, OUTPUT);
  pinMode(pinBrkA, OUTPUT);
  //initialize the Motor2
  pinMode(pinDirB, OUTPUT);
  // pinMode(pinPwmB, OUTPUT);
  pinMode(pinBrkB, OUTPUT);

  digitalWrite(pinBrkA,LOW);   //disengage the brake for Motor1
  digitalWrite(pinBrkB,LOW);   //disengage the brake for Motor2

  digitalWrite(pinDirA,LOW);
  digitalWrite(pinDirB,LOW);

  analogWrite(pinPwmA, minCurrent);
  analogWrite(pinPwmB, minCurrent);


  lastUpdateTime1 = millis();
  lastUpdateTime2 = lastUpdateTime1;
 
}

volatile int state1 = 0;
volatile int state2 = 0;


void loop()
{  
  unsigned long now = millis();
  
   //state of the shutter: HIGH=OPEN OR LOW=CLOSE
  int newState1 = digitalRead(IO1);//set the state externally
  int newState2 = digitalRead(IO2);//set the state externally

  if(newState1!=state1 || newState1!=state1)
  {
    newState1=0;
    newState2=0;
    int rounds = 128;
    for(int i=0; i<rounds; i++)
    {
      newState1 += digitalRead(IO1);
      newState2 += digitalRead(IO2);
      delayMicroseconds(1);      
    }

    if(newState1>rounds/2) 
      newState1=HIGH; 
    else 
      newState1=LOW;
    
    if(newState2>rounds/2) 
      newState2=HIGH; 
    else 
      newState2=LOW;    
    
  }

  if(newState1!=state1)
    lastUpdateTime1= now;
    
  if(newState2!=state2)
    lastUpdateTime2= now;

  state1=newState1;
  state2=newState2;
   
  if(state1)
  {
    // open:
    digitalWrite(pinDirA, HIGH);
  }
  else
  {
    // close:
    digitalWrite(pinDirA,LOW);
  } 

  if(state2)
  {
    // open:
    digitalWrite(pinDirB, HIGH);
  }
  else
  {
    // close:
    digitalWrite(pinDirB,LOW);
  } 

  
  unsigned long elapsedTime1 = now - lastUpdateTime1;
  unsigned long elapsedTime2 = now - lastUpdateTime2;
  
  if(elapsedTime1 <= pulseTime)
  {
    // otherwise we keep full power:
    analogWrite(pinPwmA, maxCurrent); 
  }
  else if(elapsedTime1 <= relaxTime)
  { 
    //if the state remains unchanged for longer than maxTime, set min power:
    int curr =  minCurrent + maxCurrent-(maxCurrent*(elapsedTime1-pulseTime))/(relaxTime-pulseTime);
    analogWrite(pinPwmA,curr); 
  }
  else if (elapsedTime1 > relaxTime)
  {
    // do nothing, already set...
  }


  if(elapsedTime2 <= pulseTime)
  {
    // otherwise we keep full power:
    analogWrite(pinPwmB, maxCurrent); 
  }
  else if(elapsedTime2 <= relaxTime)
  { 
    //if the state remains unchanged for longer than maxTime, set min power:
    int curr =  minCurrent + maxCurrent-(maxCurrent*(elapsedTime2-pulseTime))/(relaxTime-pulseTime);
    analogWrite(pinPwmB,curr); 
  }
  else if(elapsedTime2 > relaxTime)
  {
    // do nothing, already set... 
  }
}



