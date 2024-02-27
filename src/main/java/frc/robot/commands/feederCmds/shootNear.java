package frc.robot.commands.feederCmds;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.feederSubsystem;
import frc.robot.State.*;

public class shootNear extends Command {
    public feederSubsystem s_feederSubsystem;

    public shootNear(feederSubsystem feed) {
        s_feederSubsystem = feed;
      
    }

    @Override
    public void initialize() {
        s_feederSubsystem.goAState(aState.AIM_NEAR);
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}