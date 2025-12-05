package frc.robot;

import com.pathplanner.lib.auto.NamedCommands;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.trajectory.TrapezoidProfile.Constraints;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.subsystems.Swerve.SwerveSubsystem;

import java.io.File;
import swervelib.SwerveInputStream;

public class ControllerConfigurator extends RobotContainer {
    private static ControllerConfigurator instance;  // Singleton pattern
    
    public static ControllerConfigurator getInstance() {
        if (instance == null) {
            instance = new ControllerConfigurator();
        }
        return instance;
    }

    public static void configureControllerTL(RobotContainer container) {


    // ====================
    // Button bindings
    // ====================

    // Reset Gyro/Oreint robot to current facing position
    m_driverController.a().onTrue(
        Commands.sequence(
            Commands.runOnce(container.drivebase::zeroGyroAndSyncHeading),
            Commands.runOnce(container.drivebase::zeroGyroWithAlliance)
        )
    );

    // Move foward for one second
    m_driverController.y().whileTrue(
        Commands.run(() -> container.drivebase.drive(new ChassisSpeeds(0.3, 0, 0)))
    );

    // Climb Setup
    m_driverController.x().onTrue(Commands.runOnce(() -> container.climbInstance.goToRotPlusOne()));
    
    // ====================
    // Trigger Bindings
    // ====================

    // Arm control v2
    m_driverController.povRight().onTrue(Commands.runOnce(() -> container.armInstance.goToPos(Constants.ArmPositions.retracted.pos)));
    m_driverController.povUp().onTrue(Commands.runOnce(() -> container.armInstance.goToPos(Constants.ArmPositions.shootCor.pos)));
    m_driverController.povDown().onTrue(Commands.runOnce(() -> container.armInstance.goToPos(Constants.ArmPositions.grabCor.pos)));
    // m_driverController.povLeft().onTrue(Commands.runOnce(() -> Arm.goToPos(Parameters.grabCor)));

    // Arm control
    m_driverController.leftTrigger().whileTrue(Commands.runOnce(() -> container.armInstance.cMove(0.5)));
    m_driverController.leftTrigger().onFalse(Commands.runOnce(() -> container.armInstance.set(0)));
    m_driverController.rightTrigger().onTrue(Commands.runOnce(() -> container.armInstance.set(-0.75)));
    m_driverController.rightTrigger().onFalse(Commands.runOnce(() -> container.armInstance.set(0)));

    // Intake/Outake Control

    // left bumber: intake algae
    m_driverController.leftBumper().whileTrue(
      Commands.sequence(
        Commands.runOnce(() -> container.intoutInstance.set(Constants.Intout.one))
       // Commands.waitUntil(() -> Intout.algaeSwitch.get()),
      )).onFalse(
        Commands.runOnce(() -> container.intoutInstance.set(0))
      );

    // right bumper: outtake algae
    m_driverController.rightBumper().onTrue(
      Commands.sequence(
      Commands.runOnce(() -> container.intoutInstance.set(-Constants.Intout.one))
     // Commands.waitUntil(() -> !Intout.algaeSwitch.get()),
      )).onFalse(
        Commands.runOnce(() -> container.intoutInstance.set(0))
      );
  
    }
}
