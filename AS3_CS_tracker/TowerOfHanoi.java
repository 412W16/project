import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

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
import lejos.remote.nxt.BTConnection;
import lejos.remote.nxt.BTConnector;
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
	
	// tracker
	public DataOutputStream dataOut;
	public DataInputStream dataIn;
	public BTConnection BTLink;
	public BTConnection btLink;
	public double transmitReceived;
	
	// tracker values
	public double[] tar = new double[2]; // target location
	public double[] curr = new double[2]; // tracked object
	
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
		
		//connect();
		
		lift();
		bottomRing();
		place();
		
		
		
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
	
	// read the tracker
	public void readTracker() {	
		sendRequest();
		checkResponse();
	}
	
	// send a request to the tracker
	public void sendRequest() {
		try {
			//System.out.println("request made");
			dataOut.writeInt(1);
			dataOut.flush();
		}  catch (IOException ioe) {
            System.out.println("\nIO Exception writeInt");
         }
	}
	
	public void checkResponse() {
		while (true) {
			try {
				int check = dataIn.readInt();
				transmitReceived = dataIn.readDouble();
				System.out.println(transmitReceived);
				if(check == 0) {
					tar[0] = transmitReceived;
				} else if (check == 1) {
					tar[1] = transmitReceived;
				} else if (check == 2) {
					curr[0] = transmitReceived;
				} else if (check == 3) {
					curr[1] = transmitReceived;
					break;
				}
				
			} catch (IOException ioe) {
				System.out.println("IO Exception readInt");
			}
		}
	}
	
	// connect BT
	public void connect() {
		System.out.println("Listening");
		BTConnector connector = new BTConnector();
		BTLink = (BTConnection) connector.waitForConnection(100, 0);
		dataOut = BTLink.openDataOutputStream();
		dataIn = BTLink.openDataInputStream();

	}// End connect

}
