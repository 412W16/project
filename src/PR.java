
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.motor.NXTMotor;
import lejos.hardware.motor.NXTRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.EncoderMotor;

public class PR {
	// motors
	static EV3LargeRegulatedMotor leftM = new EV3LargeRegulatedMotor(MotorPort.A);
	static EV3LargeRegulatedMotor rightM = new EV3LargeRegulatedMotor(MotorPort.D);
	static NXTRegulatedMotor liftM = new NXTRegulatedMotor(MotorPort.C);
	static EV3MediumRegulatedMotor gripM = new EV3MediumRegulatedMotor(MotorPort.B);
	
	// sensors
	static EV3UltrasonicSensor ultrasonic = new EV3UltrasonicSensor(SensorPort.S2);
	static EV3GyroSensor gyro = new EV3GyroSensor(SensorPort.S1);
	static EV3ColorSensor color = new EV3ColorSensor(SensorPort.S4);
	
	static TowerOfHanoi t = new TowerOfHanoi();
	

	public static void main(String[] args) {
		t.run(leftM, rightM, liftM, gripM, ultrasonic, gyro, color);
	}
}
