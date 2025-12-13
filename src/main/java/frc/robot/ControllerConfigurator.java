package frc.robot;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.Commands;

public class ControllerConfigurator {
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
    container.getDriverController().a().onTrue(
        Commands.sequence(
            Commands.runOnce(container.getDrivebase()::zeroGyroAndSyncHeading),
            Commands.runOnce(container.getDrivebase()::zeroGyroWithAlliance)
        )
    );

    // Move foward for one second
    container.getDriverController().y().whileTrue(
        Commands.run(() -> container.getDrivebase().drive(new ChassisSpeeds(0.3, 0, 0)))
    );

    // Climb Setup
    container.getDriverController().x().onTrue(Commands.runOnce(() -> container.climbInstance.goToRotPlusOne()));
    
    // ====================
    // Trigger Bindings
    // ====================

    // Arm control v2
    container.getDriverController().povRight().onTrue(Commands.runOnce(() -> container.armInstance.goToPos(Constants.ArmPositions.retracted.pos)));
    container.getDriverController().povUp().onTrue(Commands.runOnce(() -> container.armInstance.goToPos(Constants.ArmPositions.shootCor.pos)));
    container.getDriverController().povDown().onTrue(Commands.runOnce(() -> container.armInstance.goToPos(Constants.ArmPositions.grabCor.pos)));
    // m_driverController.povLeft().onTrue(Commands.runOnce(() -> Arm.goToPos(Parameters.grabCor)));

    // Arm control
    container.getDriverController().leftTrigger().whileTrue(Commands.runOnce(() -> container.armInstance.cMove(0.5)));
    container.getDriverController().leftTrigger().onFalse(Commands.runOnce(() -> container.armInstance.set(0)));
    container.getDriverController().rightTrigger().onTrue(Commands.runOnce(() -> container.armInstance.set(-0.75)));
    container.getDriverController().rightTrigger().onFalse(Commands.runOnce(() -> container.armInstance.set(0)));

    // Intake/Outake Control

    // right bumber: intake algae
    container.getDriverController().rightBumper().whileTrue(
      Commands.sequence(
        Commands.runOnce(() -> container.intoutInstance.set(Constants.Intout.one))
       // Commands.waitUntil(() -> Intout.algaeSwitch.get()),
      )).onFalse(
        Commands.runOnce(() -> container.intoutInstance.set(0))
      );

    // left bumper: outtake algae
    container.getDriverController().leftBumper().onTrue(
      Commands.sequence(
      Commands.runOnce(() -> container.intoutInstance.set(-Constants.Intout.one))
     // Commands.waitUntil(() -> !Intout.algaeSwitch.get()),
      )).onFalse(
        Commands.runOnce(() -> container.intoutInstance.set(0))
      );
  
    }

    public static void configureControllerSim(RobotContainer container) {

      container.getDriverController().start().onTrue(Commands.runOnce(() -> container.getDrivebase().resetOdometry(new Pose2d(3, 3, new Rotation2d()))));

      container.getDriverController().button(1).whileTrue(container.getDrivebase().sysIdDriveMotorCommand());

      container.getDriverController().button(2).whileTrue(Commands.runEnd(() -> container.driveDirectAngleKeyboard.driveToPoseEnabled(true),
      () -> container.driveDirectAngleKeyboard.driveToPoseEnabled(false)));
      //  driverXbox.b().whileTrue(
      //      drivebase.driveToPose(
      //          new Pose2d(new Translation2d(4, 4), Rotation2d.fromDegrees(0)))
      //                          );
    }

    public static void configureControllerTest(RobotContainer container) {}
}
