package frc.robot.subsystems.Score;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.ClosedLoopSlot;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.ClosedLoopConfig.FeedbackSensor;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class Intout extends SubsystemBase {
    private final static SparkMax intout = new SparkMax(Constants.Intout.motorID, com.revrobotics.spark.SparkLowLevel.MotorType.kBrushless);
    public static SparkClosedLoopController CLcontroller = intout.getClosedLoopController();
    private final SparkMaxConfig config = new SparkMaxConfig();
    private final RelativeEncoder encoder = intout.getEncoder();

    public final static DigitalInput coralSwitch = new DigitalInput(Constants.Intout.coralLSID);
    public final static DigitalInput algaeSwitch = new DigitalInput(Constants.Intout.algaeLSID);
    public static Intout mInstance = null;
    public static Intout getInstance() {
        if (mInstance == null) {
            mInstance = new Intout();
        }
        return mInstance;
    }

    public Intout() {
        // reset to fac defaults?
        intout.configure(new SparkMaxConfig(), ResetMode.kResetSafeParameters, PersistMode.kNoPersistParameters);

        encoder
        .setPosition(0);

        config
        .inverted(false);

        config
        .idleMode(IdleMode.kBrake);

        config
        .smartCurrentLimit(60);

        config.encoder
        .positionConversionFactor(1)
        .velocityConversionFactor(1);

        config.closedLoop
        .feedbackSensor(FeedbackSensor.kPrimaryEncoder)
        .p(Constants.Intout.pid[0])
        .i(Constants.Intout.pid[1])
        .d(Constants.Intout.pid[2])
        .outputRange(-1, 1)
        // Set PID values for velocity control in slot 1
        .p(Constants.Intout.pid[0], ClosedLoopSlot.kSlot1) // 0.05
        .i(Constants.Intout.pid[1], ClosedLoopSlot.kSlot1)
        .d(Constants.Intout.pid[2], ClosedLoopSlot.kSlot1)
        .velocityFF(Constants.Intout.velFF, ClosedLoopSlot.kSlot1)
        .outputRange(-1, 1, ClosedLoopSlot.kSlot1);
        
        intout.configure(config, ResetMode.kNoResetSafeParameters, PersistMode.kNoPersistParameters);
    }

    private double currentTarg = 0;

    public void stopMotor() {
        intout.stopMotor();
        currentTarg = 0;
    }

    public void goToPos(double targPos) {
        CLcontroller.setReference(targPos, ControlType.kPosition, ClosedLoopSlot.kSlot0);
        currentTarg = targPos;
    }

    public double getTargetPos() {
        return currentTarg;
    }

    public double getPos() {
        double position = intout.getEncoder().getPosition();
            return position;
    }

    public boolean atTarget() {
        // Only check if a target has been set (and isn't zero)
        if (getTargetPos() != 0) {
            System.out.println("No target set!");
        }
        return Math.abs(getPos() - getTargetPos()) < 0.5 && getTargetPos() != 0;
    }

    public void set(double speed) {
        // CLcontroller.setReference(targetVel, ControlType.kVelocity);
        intout.set(speed);
    }

    @Override
    public void periodic() {
        double error = Math.abs(getPos() - currentTarg);
        if (error < 0.5 && currentTarg != 0) {
            // Stop motor once target reached
            intout.stopMotor();
            encoder.setPosition(0);  // Zero encoder position after target reached
        }
    }    
}