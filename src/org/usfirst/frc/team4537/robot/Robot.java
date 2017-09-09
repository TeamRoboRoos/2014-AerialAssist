package org.usfirst.frc.team4537.robot;

import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
//import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
//import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This is a demo program showing the use of the RobotDrive class. The
 * SampleRobot class is the base of a robot application that will automatically
 * call your Autonomous and OperatorControl methods at the right time as
 * controlled by the switches on the driver station or the field controls.
 *
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SampleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 *
 * WARNING: While it may look like a good choice to use for your code if you're
 * inexperienced, don't. Unless you know what you are doing, complex code will
 * be much more difficult under this system. Use IterativeRobot or Command-Based
 * instead if you're new.
 */
public class Robot extends SampleRobot {
	Compressor compressor = new Compressor(10);
	Solenoid shooterSolenoid = new Solenoid(10, 1);
	DoubleSolenoid BASSolenoid = new DoubleSolenoid(10, 2, 3);
	AnalogInput pressure = new AnalogInput(0);
	DigitalInput BASSwitch = new DigitalInput(0);

	Joystick stickL = new Joystick(0);
	Joystick stickR = new Joystick(1);
	Joystick controller = new Joystick(2);

	Talon motorLF = new Talon(2);
	Talon motorLB = new Talon(3);
	Talon motorRF = new Talon(0);
	Talon motorRB = new Talon(1);
	Victor motorBAS = new Victor(4);

	int BASBallIn=2, BASBallOut=3, shoot=1, shoot2=11, shootRst=4;
	int BASStickAxis = 1;
	double BASMotorSpeed = 0.9;

	RobotDrive driveBase = new RobotDrive(motorLB, motorLF, motorRB, motorRF);

	boolean bShoot=false, bHasShot=false, bBASL=false, bBASR=false, bShootRst=false;
	boolean vButtonBASL=false, vButtonBASR=false;
	long timer0 = 0;

	//	final String defaultAuto = "Default";
	//	final String customAuto = "My Auto";
	//	SendableChooser<String> chooser = new SendableChooser<>();

	public Robot() {
		driveBase.setExpiration(0.1);
	}

	@Override
	public void robotInit() {
		motorLF.setInverted(false);
		motorLF.setExpiration(0.1);
		motorLB.setInverted(false);
		motorLB.setExpiration(0.1);
		motorRF.setInverted(false);
		motorRF.setExpiration(0.1);
		motorRB.setInverted(false);
		motorRB.setExpiration(0.1);

		//		chooser.addDefault("Default Auto", defaultAuto);
		//		chooser.addObject("My Auto", customAuto);
		//		SmartDashboard.putData("Auto modes", chooser);
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the
	 * if-else structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */
	@Override
	public void autonomous() {
		//		String autoSelected = chooser.getSelected();
		//		// String autoSelected = SmartDashboard.getString("Auto Selector",
		//		// defaultAuto);
		//		System.out.println("Auto selected: " + autoSelected);
		//
		//		switch (autoSelected) {
		//		case customAuto:
		//			driveBase.setSafetyEnabled(false);
		//			driveBase.drive(-0.5, 1.0); // spin at half speed
		//			Timer.delay(2.0); // for 2 seconds
		//			driveBase.drive(0.0, 0.0); // stop robot
		//			break;
		//		case defaultAuto:
		//		default:
		//			driveBase.setSafetyEnabled(false);
		//			driveBase.drive(-0.5, 0.0); // drive forwards half speed
		//			Timer.delay(2.0); // for 2 seconds
		//			driveBase.drive(0.0, 0.0); // stop robot
		//			break;
		//		}
	}

	/**
	 * Runs the motors with arcade steering.
	 */
	@Override
	public void operatorControl() {
		driveBase.setSafetyEnabled(true);
		while (isOperatorControl() && isEnabled()) {

			if(stickL.getName().equalsIgnoreCase("Controller (XBOX 360 For Windows)")) {
				driveBase.tankDrive(-stickL.getRawAxis(1), -stickL.getRawAxis(5));
				System.out.println("L: " + stickL.getRawAxis(1) + " R: " + stickL.getRawAxis(5));
			}
			else {
				driveBase.tankDrive(-stickL.getY(), -stickR.getY(), true);
			}

			if(controller.getRawAxis(BASStickAxis) >= 0.67) vButtonBASL = true;
			else vButtonBASL = false;
			if(controller.getRawAxis(BASStickAxis) <= -0.67) vButtonBASR = true;
			else vButtonBASR = false;

			if(controller.getRawButton(shoot) && controller.getRawButton(shoot2) && !bShoot && BASSwitch.get()) {
				bShoot = true;
				bHasShot = true;
				shooterSolenoid.set(true);
			}
			else if(!controller.getRawButton(shoot)) bShoot = false;

			if(controller.getRawButton(shootRst) && !bShootRst) {
				bShootRst = true;
				bHasShot = false;
				shooterSolenoid.set(false);
			}
			else if(!controller.getRawButton(shootRst)) bShootRst = false;

			if(vButtonBASL && !bBASL && !bHasShot) {
				bBASL = true;
				timer0 = System.currentTimeMillis();
				BASSolenoid.set(DoubleSolenoid.Value.kForward);
			}
			else if(!vButtonBASL) bBASL = false;

			if(vButtonBASR && !bBASR) {
				bBASR = true;
				timer0 = 0;
				BASSolenoid.set(DoubleSolenoid.Value.kReverse);
			}
			else if(!vButtonBASR) bBASR = false;

			if(controller.getRawButton(BASBallIn)) motorBAS.set(BASMotorSpeed);
			else if(controller.getRawButton(BASBallOut)) motorBAS.set(-BASMotorSpeed);
			else motorBAS.set(0);


			SmartDashboard.putBoolean("DB/LED 0", BASSwitch.get());
			SmartDashboard.putString("DB/String 0", "Pressure:");
			SmartDashboard.putString("DB/String 5", Double.toString(pressure.getValue()/1+0)+"psi"); //Calibration /Value and +Value
			

			Timer.delay(0.005); // wait for a motor update time
		}
	}

	/**
	 * Runs during test mode
	 */
	@Override
	public void test() {
	}
}
