// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.robot.Constants;
import frc.robot.commands.Autos;
import frc.robot.commands.ExampleCommand;
import frc.robot.subsystems.ExampleSubsystem;
import frc.robot.subsystems.SwerveSubsystem;
import swervelib.SwerveInputStream;

import java.io.File;

import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;

/**
 * This class is where the bulk of the robot should be declared. Silence Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */

public class RobotContainer {
  // The robot's subsystems and commands are defined here...
  private final ExampleSubsystem m_exampleSubsystem = new ExampleSubsystem();
  private final SwerveSubsystem driveBase =new SwerveSubsystem(new File(Filesystem.getDeployDirectory(), "swerve"));


  // Replace with CommandPS4Controller or CommandJoystick if needed
  private final CommandXboxController m_driverController =
      new CommandXboxController(Constants.Controller.kDriverControllerPort);

  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {
    // Configure the trigger bindings
    configureBindings();
    driveBase.setDefaultCommand(driveFeildOrientedAngularVelocity);
  }

  SwerveInputStream driveAngularVelocity = SwerveInputStream.of(
    driveBase.getSwerveDrive(), 
    () -> m_driverController.getLeftY() * -1,
    () -> m_driverController.getLeftX() * -1)
    .withControllerRotationAxis(m_driverController::getRightX)
    .deadband(Constants.Controller.deadband)
    .scaleTranslation(Constants.Controller.scaleTranslation)
    .allianceRelativeControl(true);

    SwerveInputStream driveDirectAngle = driveAngularVelocity.copy()
    .withControllerHeadingAxis(m_driverController::getRightX, m_driverController::getRightY)
    .headingWhile(true);

  Command driveFieldOrientedDirectAngle = driveBase.driveFieldOriented(driveDirectAngle);
  Command driveFeildOrientedAngularVelocity = driveBase.driveFieldOriented(driveAngularVelocity);

  private void configureBindings() {
    m_driverController.b().whileTrue(m_exampleSubsystem.exampleMethodCommand());
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // An example command will be run in autonomous
    return Autos.exampleAuto(m_exampleSubsystem);
  }
}
