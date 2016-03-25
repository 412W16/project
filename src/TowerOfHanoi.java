import lejos.hardware.Button;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.motor.NXTMotor;
import lejos.hardware.motor.NXTRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.EncoderMotor;
import lejos.utility.Delay;

public class TowerOfHanoi {
	
	// motors
	static EV3LargeRegulatedMotor leftM;
	static EV3LargeRegulatedMotor rightM;
	static NXTRegulatedMotor liftM;
	static EV3MediumRegulatedMotor gripM;
	
	// sensors
	static EV3UltrasonicSensor ultrasonic;
	static EV3GyroSensor gyro;
	static EV3ColorSensor color;
	
	// constants of puzzle
	
	// distance between poles (cm)
	double poleDist = 15.0;
	// distance from ultrasonic to gripper (cm)
	double gripDist = 15.0;
	
	public void run(EV3LargeRegulatedMotor _leftM, EV3LargeRegulatedMotor _rightM, NXTRegulatedMotor _liftM,
			EV3MediumRegulatedMotor _gripM, EV3UltrasonicSensor _ultrasonic, EV3GyroSensor _gyro, EV3ColorSensor _color) {
		
		// assign motors and sensors
		leftM = _leftM;
		rightM = _rightM;
		liftM = _liftM;
		gripM = _gripM;
		ultrasonic = _ultrasonic;
		gyro = _gyro;
		color = _color;
		
		lift();
		//bottomRing();
		//place();
		while(true){
			distReading();
			Delay.msDelay(1000);
		}
		
		
		//Button.waitForAnyPress();
	}
	
	// put the gripper into the lift position
	public void lift() {
		liftM.setSpeed(50);
		liftM.rotateTo(80);
	}
	
	// grab the bottom ring
	public void bottomRing() {
		liftM.setSpeed(50);
		liftM.rotateTo(15);
		
		grip();
		lift();
		//drop();
	}
	
	// grab the middle ring
	public void middleRing() {
		liftM.setSpeed(50);
		liftM.rotateTo(25);
		
		grip();
		lift();
		//drop();
	}
	
	// grab the top ring
	public void topRing() {
		liftM.setSpeed(50);
		liftM.rotateTo(40);
		
		grip();
		lift();
		//drop();
	}
	
	// place a ring on a pole
	public void place() {
		liftM.setSpeed(50);
		liftM.rotateTo(50);
		drop();
	}
	
	// grip a ring
	public void grip() {
		gripM.setSpeed(200);
		gripM.rotateTo(-600);
	}
	
	// release grip/drop 
	public void drop() {
		gripM.setSpeed(180);
		gripM.rotateTo(0);
	}
	
	// returns a reading from the ultrasonic sensor
	public float[] distReading() {
		float[] dist = new float[1];
		ultrasonic.getDistanceMode().fetchSample(dist, 0);
		System.out.format("%f", dist[0]);
		return dist;
	}

}
