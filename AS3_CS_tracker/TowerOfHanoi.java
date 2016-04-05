import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.hardware.Button;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.motor.NXTRegulatedMotor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.remote.nxt.BTConnection;
import lejos.remote.nxt.BTConnector;
import lejos.utility.Delay;
import java.awt.Point;

public class TowerOfHanoi {
	
	// motors
	static EV3LargeRegulatedMotor leftM;
	static EV3LargeRegulatedMotor rightM;
	static NXTRegulatedMotor liftM;
	static EV3MediumRegulatedMotor gripM;
	
	// sensors
	static EV3UltrasonicSensor ultrasonic;
	static EV3GyroSensor gyro;
	
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
	public Point pole1 = new Point(585, 200);
	public Point pole2 = new Point(335, 200);
	public Point pole3 = new Point(85, 200);
	private Tower[] towers = new Tower[3];
	
	public int currentPos = 1;
	public Boolean pickedUp = false;
	
	public boolean stop = false;
	
	public void run(EV3LargeRegulatedMotor _leftM, EV3LargeRegulatedMotor _rightM, NXTRegulatedMotor _liftM,
			EV3MediumRegulatedMotor _gripM, EV3UltrasonicSensor _ultrasonic, EV3GyroSensor _gyro) {
		
		// assign motors and sensors
		leftM = _leftM;
		rightM = _rightM;
		liftM = _liftM;
		gripM = _gripM;
		ultrasonic = _ultrasonic;
		gyro = _gyro;
//		drop();
		lift();
		connect();
		initTowers();
		initRings();
		align(pole1);
		solve(3, towers[0], towers[1], towers[2]);
		
		turnRight();
		turnRight();
		turnRight();
		turnRight();
		turnRight();
		turnRight();
		
		Button.waitForAnyPress();
	}
		
	private void initRings() {
		towers[0].addRing(new Ring("Blue", 100));
		towers[0].addRing(new Ring("Green", 50));
		towers[0].addRing(new Ring("Yellow", 25));
	}
	
	private void initTowers() {
		towers[0] = new Tower("1", 0);
		towers[1] = new Tower("2", 0);
		towers[2] = new Tower("3", 0);
	}
	
	private void pickUp() {
		int height = towers[currentPos-1].getHeight();

		if (height == 3) {
			topRing();
		} else if (height == 2) {
			middleRing();
		} else if (height == 1) {
			bottomRing();
		} else {
			System.out.println("ERROR NO RINGS TO PICKUP");
		}
		
		pickedUp = true;
	}
	
	private void putDown() {
		int height = towers[currentPos-1].getHeight();
		
		if (height == 3) {
			System.out.println("ERROR TOWER TOO TALL");
		} else if (height == 2) {
			placeTop();
		} else if (height == 1) {
			placeMid();
		} else {
			placeBot();
		}
		
		pickedUp = false;
	}
	
	// action => 0 pickup, 1 drop
	private void moveRobot(Tower from, Tower to, Boolean action) {
		int toInt = Integer.parseInt(to.getNumber());
		int fromInt = Integer.parseInt(from.getNumber());
		
		if (fromInt != currentPos) {
			moveRobot(towers[currentPos-1], from, false);
		} else if (action && !pickedUp) {
			pickUp();
		}
		
		if (toInt - fromInt == 1){
			//from 1 -> 2, or 2 -> 3
			moveBackward();
			turnRight();
			moveForward();
			turnLeft();
		} else if (toInt - fromInt == 2){
			//from 1 -> 3
			moveBackward();
			turnRight();
			moveForward();
			rightM.forward();
			Delay.msDelay(200);
			rightM.stop();
			moveForward();
			rightM.forward();
			Delay.msDelay(200);
			rightM.stop();
			turnLeft();
			// try moving into frame more
			leftM.setSpeed(80);
			rightM.setSpeed(80);
			
			leftM.forward();
			rightM.forward();
			
			Delay.msDelay(700);
			
			leftM.stop();
			rightM.stop();
		} else if (toInt - fromInt == -1){
			//from 3 -> 2, or 2 -> 1
			moveBackward();
			turnLeft();
			moveForward();
			turnRight();
		} else if (toInt - fromInt == -2){
			//from 3 -> 1
			moveBackward();
			turnLeft();
			moveForward();
			moveForward();
			turnRight();
			// try moving into frame more
			leftM.setSpeed(80);
			rightM.setSpeed(80);
			
			leftM.forward();
			rightM.forward();
			
			Delay.msDelay(700);
			
			leftM.stop();
			rightM.stop();
		}
		
		if (toInt == 1) {
			align(pole1);
		} else if (toInt == 2) {
			align(pole2);
		} else {
			align(pole3);
		}

		currentPos = toInt;
		
		if (action) {
			putDown();
		} else {
			pickUp();
		}
		
		return;
	}
	
	private void moveRing(Tower from, Tower to) {
		Ring r = from.removeRing();
		System.out.println("Moving " + r.getColor() + 
				" from tower " + from.getNumber() + 
				" to tower " + to.getNumber());
		to.addRing(r);
	}
	
	private void solve(int height, Tower from, Tower to, Tower with) {
		if (height >= 1) {
			solve(height-1, from, with, to);
			moveRobot(from, to, true);
			moveRing(from, to);
			solve(height-1, with, to, from);
		}
	}
	
	private void align(Point pole) {
		// make sure your tracking the arm!!!
		Button.waitForAnyPress();
		
		readTracker();
		
		Point error = new Point((int)curr[0] - pole.x, (int)curr[1] - pole.y);
		
		double buffer = 10;
		long delay = 100;
		
		while (Math.abs(error.x) > buffer || Math.abs(error.y) > buffer) {
			// correct x error
			
			// turn right
			if (error.x > buffer){
				leftM.setSpeed(80);
				rightM.setSpeed(80);
				
				leftM.forward();
				rightM.backward();
				
				Delay.msDelay(delay);
				
				leftM.stop();
				rightM.stop();
			// turn left
			} else if (error.x < -buffer){
				leftM.setSpeed(80);
				rightM.setSpeed(80);
				
				leftM.backward();
				rightM.forward();
				
				Delay.msDelay(delay);
				
				leftM.stop();
				rightM.stop();
			}
			//correct y error
			
			// move forward
			if (error.y < -buffer) {
				leftM.setSpeed(80);
				rightM.setSpeed(80);
				
				leftM.forward();
				rightM.forward();
				
				Delay.msDelay(delay);
				
				leftM.stop();
				rightM.stop();
			// move backwards
			} else if (error.y > buffer) {
				leftM.setSpeed(80);
				rightM.setSpeed(80);
				
				leftM.backward();
				rightM.backward();
				
				Delay.msDelay(delay);
				
				leftM.stop();
				rightM.stop();
			}
			
			// re-read the current value and compute new error
			readTracker();
			
			error = new Point((int)curr[0] - pole.x, (int)curr[1] - pole.y);
		}
	}

	public void turnRight() {
		leftM.setSpeed(80);
		rightM.setSpeed(80);
		
		leftM.forward();
		rightM.backward();
		
		float start = readGyro();
		while (start - readGyro() <= 85.0 ) {
			continue;
		}
		
		leftM.stop();
		rightM.stop();
	}
	
	
	public void moveForward() {
		leftM.setSpeed(80);
		rightM.setSpeed(80);
		
		leftM.forward();
		rightM.forward();
		
		Delay.msDelay(3200);
		
		
		leftM.stop();
		rightM.stop();
		
		
	}
	
	public void moveBackward() {
		leftM.setSpeed(80);
		rightM.setSpeed(80);
		
		leftM.backward();
		rightM.backward();
		
		Delay.msDelay(1700);
		
		leftM.stop();
		rightM.stop();
	}
	
	public void turnLeft(){
		leftM.setSpeed(80);
		rightM.setSpeed(80);
		
		leftM.backward();
		rightM.forward();
		
		float start = readGyro();
		while (readGyro() - start <= 85.0 ) {
			continue;
		}
		
		rightM.stop();
		leftM.stop();
	}
	
	public float readGyro() {
		float[] deg = new float[1];
		
		gyro.getAngleMode().fetchSample(deg, 0);
		
		return deg[0];
	}
	
	// put the gripper into the lift position
	public void lift() {
		liftM.setSpeed(50);
		liftM.rotateTo(60);
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
		liftM.rotateTo(27);
		
		grip();
		lift();
		//drop();
	}
	
	// grab the top ring
	public void topRing() {
		liftM.setSpeed(50);
		liftM.rotateTo(39);
		
		grip();
		lift();
		//drop();
	}
	
	// place a ring on a pole
	public void placeTop() {
		liftM.setSpeed(50);
		liftM.rotateTo(35);
		drop();
		lift();
	}
	
	public void placeMid() {
		liftM.setSpeed(50);
		liftM.rotateTo(20);
		drop();
		lift();
	}
	
	public void placeBot() {
		liftM.setSpeed(50);
		liftM.rotateTo(15);
		drop();
		lift();
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
				System.out.print("x: ");
				System.out.println(tar[0]);
				System.out.print("y:");
				System.out.println(tar[1]);
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
