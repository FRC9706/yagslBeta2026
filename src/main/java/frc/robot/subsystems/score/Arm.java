package frc.robot.subsystems.score;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkClosedLoopController;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

import com.revrobotics.spark.config.ClosedLoopConfig.FeedbackSensor;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.ClosedLoopSlot;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;

public class Arm extends SubsystemBase {

    public static Arm mInstance = null;
    public static Arm getInstance(){
        if(mInstance==null){
            mInstance = new Arm();
        }
        return mInstance;
    }

    static private final SparkMax motor = new SparkMax(Constants.Arm.motorID, MotorType.kBrushless);
    public static SparkClosedLoopController CLcontroller = motor.getClosedLoopController();
    private final SparkMaxConfig config = new SparkMaxConfig();
    private final RelativeEncoder encoder = motor.getEncoder();
    // static public final SparkClosedLoopController cloop = motor.getClosedLoopController();
    // private boolean algaePrepared = false;

    public Arm() {
        encoder
        .setPosition(-6.786);

        config
        .inverted(false);

        config
        .idleMode(IdleMode.kBrake);

        config
        .smartCurrentLimit(10);

        config.encoder
        .positionConversionFactor(1)
        .velocityConversionFactor(1);

        config.closedLoop
        .feedbackSensor(FeedbackSensor.kPrimaryEncoder)
        .p(Constants.Arm.pid[0])
        .i(Constants.Arm.pid[1])
        .d(Constants.Arm.pid[2])
        .outputRange(Constants.Arm.minOut, Constants.Arm.mxOut)
        // Set PID values for velocity control in slot 1
        .p(Constants.Arm.pid[0], ClosedLoopSlot.kSlot1) // 0.0001
        .i(Constants.Arm.pid[1], ClosedLoopSlot.kSlot1)
        .d(Constants.Arm.pid[2], ClosedLoopSlot.kSlot1)
        .velocityFF(Constants.Arm.velFF, ClosedLoopSlot.kSlot1)
        .outputRange(Constants.Arm.minOut, Constants.Arm.mxOut, ClosedLoopSlot.kSlot1);

        motor.configure(config, ResetMode.kNoResetSafeParameters, PersistMode.kNoPersistParameters);
    }

    public boolean isOverLim = false;
    public boolean isMoving = false;

    public static void goToPos(double targPos) {
        CLcontroller.setReference(targPos, ControlType.kPosition, ClosedLoopSlot.kSlot0);
    }

    public void cMove(double sped) {
        if (!isOverLim) {
            motor.set(sped);
            isMoving = true;
        } else {
            System.out.println("STOP MOVING THE ARM ANDRE");
            motor.stopMotor();
            isMoving = false;
        }
    }

    public static double getPos() {
        double position = motor.getEncoder().getPosition();
            return position;
    }
    
    

    // public void setTargetPos(double pos) {
    //     cloop.setReference(pos, SparkMax.ControlType.kPosition);
    // }

    public void set(double sped) {
        motor.set(sped);
        System.out.println("I just set ur arm to the speed: " + sped);
    }

    // public void prepareForAlgae() {
    //     if (algaePrepared){
    //     config.idleMode(IdleMode.kCoast);
    //     motor.configure(config, ResetMode.kNoResetSafeConstants.Arm, PersistMode.kNoPersistConstants.Arm);
    //     algaePrepared = false;
    // } else {
    //     setTargetPos(Constants.Arm.kArmAlgae);
    //     config.idleMode(IdleMode.kBrake);
    //     motor.configure(config, ResetMode.kNoResetSafeConstants.Arm, PersistMode.kNoPersistConstants.Arm);
    //     algaePrepared = true;
    // }
    // }

    // public void AutoGoToGround() {
    //     setTargetPos(Constants.Arm.kArmPos1);
    // }

    // public void AutoGoUp() {
    //     setTargetPos(Constants.Arm.kArmPos2);
    // }

    @Override
    public void periodic() {
        if ((Math.abs(getPos())) < 10) {
            if (isMoving) {
            motor.stopMotor();
            isMoving = false;
            }
            isOverLim = true;
        } else {
            isOverLim = false;
        }
    }
}