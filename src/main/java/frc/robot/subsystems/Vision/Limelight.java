package frc.robot.subsystems.Vision;

import edu.wpi.first.math.geometry.Pose2d;
import frc.robot.subsystems.Vision.LimelightHelpers;
import frc.robot.subsystems.Vision.LimelightHelpers.PoseEstimate;

public class Limelight {
   PoseEstimate pose = LimelightHelpers.getBotPoseEstimate_wpiBlue("limelight");
}
