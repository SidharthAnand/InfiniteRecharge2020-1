package frc.robot.subsystems.shooter;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.team7419.Initers;
import com.team7419.MotorGroup;
import com.team7419.TalonFuncs;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.CanIds;;

public class ShooterSub extends SubsystemBase{

    private VictorSPX victor;
	public TalonSRX talon;
    public MotorGroup motors;
    private double kRampingF;
    public double powerOutput = 0.2;
    public double kP = 0;
    public double kI = 0;
    public double kD = 0;
    public double kF = 0;
    public double targetVelocity = 0;
    public double rawSpeed = 500;
    public ControlMethod controlMethod = ControlMethod.PERCENT_OUTPUT;

    public ShooterSub(){

        victor = new VictorSPX(CanIds.rightVictor.value);
	    talon = new TalonSRX(CanIds.rightTalon.value);

        motors = new MotorGroup(talon, victor);

        Initers.initVictors(victor);
        motors.followMaster();
        talon.neutralOutput();
	    talon.setSensorPhase(true);
        talon.configNominalOutputForward(0, 0);
	    talon.configNominalOutputReverse(0, 0);
        talon.configClosedloopRamp(.2, 0);
    
        TalonFuncs.configEncoder(talon);
    }

    public enum ControlMethod{
        PERCENT_OUTPUT,
        SPIN_UP,
        HOLDING,
    }

    @Override
    public void periodic() {
    }

    public void run(){
        ControlMethod method = this.controlMethod;
        if(method == ControlMethod.PERCENT_OUTPUT){
            talon.set(ControlMode.PercentOutput, powerOutput);
            System.out.println("spamming but working");
        }
        else if(method == ControlMethod.HOLDING){
            feedforwardOnly();
        }
        else if(method == ControlMethod.SPIN_UP){
            this.setPIDF(kP, kI, kD, kF);
            talon.set(ControlMode.Velocity, rawSpeed);
        }
    }

    public void reset(){
        talon.configFactoryDefault(); // so nothing acts up
        talon.getSensorCollection().setQuadraturePosition(0, 10); // reset encoder values
        TalonFuncs.configEncoder(talon);
    }

    public void configureOutputs(){
        talon.configNominalOutputForward(0, 0);
		talon.configNominalOutputReverse(0, 0);
		talon.configPeakOutputForward(1, 0);
        talon.configPeakOutputReverse(-1, 0);
    }

    public void setPIDF(double kP, double kI, double kD, double kF){
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
        this.kF = kF;
        TalonFuncs.setPIDFConstants(0, talon, kP, kI, kD, kF);
    }

    public void setOutputPower(double power){this.powerOutput = power;}

    public void setkF(double kF){this.kF = kF;}

    public void setTargetRpm(double rpm){this.rawSpeed = rpm * 1.7067;}

    public void setControlMethod(ControlMethod method){
        this.controlMethod = method;
    }

    public double getCurrentRawSpeed(){return talon.getSelectedSensorVelocity();}

    public void setTargetRawSpeed(double speed){this.rawSpeed = speed;}

    public void percentOutput(){
        motors.setPower(powerOutput);
    }

    public void feedforwardOnly(){
        this.setPIDF(0.0, 0.0, 0.0, kF);
        talon.set(ControlMode.Velocity, rawSpeed);
    }

    public double getkP(){return kP;}
    public double getkI(){return kI;}
    public double getkD(){return kD;}
    public double getkF(){return kF;}

}