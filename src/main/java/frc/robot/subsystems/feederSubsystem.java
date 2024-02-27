package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkBase.IdleMode;
import com.revrobotics.CANSparkFlex;
import com.revrobotics.CANSparkLowLevel;
import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.math.controller.ArmFeedforward;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.State.aState;
import frc.robot.State.fState;
import frc.robot.State.sState;
import frc.robot.Constants;

import frc.lib.util.CANSparkMaxUtil;
import frc.lib.util.CANSparkMaxUtil.Usage;

public class feederSubsystem extends SubsystemBase {
    public CANSparkFlex m_LeftFeederMotor;
    public CANSparkFlex m_RightFeederMotor;
    public fState fstate;

    private double spinSpeed;
    private CANSparkMaxUtil canSparkMaxUtil;

    //ARM MOVEMENT
    public CANSparkFlex m_RightAimingMotor;
    public CANSparkFlex m_LeftAimingMotor;
    public sState sState; 
    public aState aState;

    public DutyCycleEncoder a_Encoder;

    private double armSpeed;

    private PIDController aPID;
    private ArmFeedforward aFeedforward;

    private double aPV; //curr position
    private double aSetPoint; //destination we want to go to

    //POSE PARAMETERS
    double toHome;
    double toIntake;
    double toTrap;
    double toFar;//Arbitrary value based on distance, shoots
    double toNear;//Arbitrary value based on distance, shoots
    

    public feederSubsystem(){
        canSparkMaxUtil = new CANSparkMaxUtil();

        //SPINNER MOVEMENT
        m_LeftFeederMotor = new CANSparkFlex(Constants.feederSubsystem.leftMotorID, MotorType.kBrushless); //Fixed, Had to Reconfigure Motor 21
        m_RightFeederMotor = new CANSparkFlex(Constants.feederSubsystem.rightMotorID, MotorType.kBrushless);


        //ARM MOVEMENT
        m_RightAimingMotor = new CANSparkFlex(frc.robot.Constants.shooterAimingSystem.m_aim1, MotorType.kBrushless);
        m_LeftAimingMotor = new CANSparkFlex(frc.robot.Constants.shooterAimingSystem.m_aim2, MotorType.kBrushless);

        m_RightAimingMotor.setIdleMode(IdleMode.kCoast);
        m_LeftAimingMotor.setIdleMode(IdleMode.kCoast);

        a_Encoder = new DutyCycleEncoder(frc.robot.Constants.feederSubsystem.feederEncoderID); //PWM Channel
        
        double ffP = 0;
        double ffI = 0;
        double ffD = 0;
        aPID = new PIDController(ffP, ffI, ffD);

        aFeedforward = new ArmFeedforward(0, 0.2, 0); //TODO: Tune Feeder Feedforward

        


        //ARM SETPOINTS
        toIntake = 0; //TODO: calibrate Feeder ARM Setpoints
        toTrap = 0; 
        toFar = 0;
        toNear = 0;

        //CANBUS USAGE CONSTRAINTS
        m_LeftFeederMotor.setPeriodicFramePeriod(CANSparkLowLevel.PeriodicFrame.kStatus2, 20); //TODO: Set Appropriate CanSparkFlex Bus Usage

        fstate = frc.robot.State.fState.STOP;
        sState = frc.robot.State.sState.STOP;
    }

    private double aPos() {
        return a_Encoder.getAbsolutePosition() * 360;
    }


    @Override
    public void periodic(){
        //SPINNER
        m_LeftFeederMotor.set(spinSpeed);

        m_RightFeederMotor.set(spinSpeed);

        //ARM
        aPV = aPos();
        double aOutput = aPID.calculate(aPV, aSetPoint);
        m_RightAimingMotor.set(aOutput);
        m_LeftAimingMotor.set(aOutput);

        //SmartDashboard.putNumber("Arm Encoder Rot:", aPos());

    }

    public double FPos1(){
        return m_LeftFeederMotor.getEncoder().getPosition();
    }

    public double FPos2(){
        return m_RightFeederMotor.getEncoder().getPosition();
    }


    //FLYWHEEL SPIN STATE
    public void gosState(sState state){
        if(sState == frc.robot.State.sState.OUT)
        {
            spinSpeed = 0.5;
        }

        if(sState == frc.robot.State.sState.IN)
        {
            spinSpeed = -0.5;
        }

        if(sState == frc.robot.State.sState.STOP)
        {
            spinSpeed = 0.5;
        }
    }
    

    //AIM SPIN STATE
    public void goFstate(fState state){
        if(fstate == frc.robot.State.fState.OUT)
        {
            spinSpeed = 0.5;
        }

        if(fstate == frc.robot.State.fState.IN)
        {
            spinSpeed = -0.5;
        }

        if(fstate == frc.robot.State.fState.STOP)
        {
            spinSpeed = 0.5;
        }
    }


    //ARM SET SETPOINT
    public void setASetPoint(double setpoint){
        aSetPoint = setpoint;
    }

    //ARM MOVEMENT STATE
     public void goAState(aState state){ 
        if (state == frc.robot.State.aState.INTAKE_POS) {
            setASetPoint(toIntake);
            aState = frc.robot.State.aState.INTAKE_POS;
             
        }
        if (state == frc.robot.State.aState.TRAP_POS) {
            //she P on my I till i D
            setASetPoint(toTrap);
            aState = frc.robot.State.aState.TRAP_POS;
        }
        if (state == frc.robot.State.aState.AIM_FAR) {
            //she P on my I till i D
            setASetPoint(toFar);
            aState = frc.robot.State.aState.AIM_FAR;
        }
        if (state == frc.robot.State.aState.AIM_NEAR) {
            //she P on my I till i D
            setASetPoint(toNear);
            aState = frc.robot.State.aState.AIM_NEAR;
        }
    }

    public void stopWheels(){
        goFstate(frc.robot.State.fState.STOP);
    }
}
