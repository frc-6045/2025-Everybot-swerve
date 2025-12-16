# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Build the robot code
./gradlew build

# Deploy to the robot (requires connection to roboRIO)
./gradlew deploy

# Run simulation with GUI
./gradlew simulateJava

# Run tests
./gradlew test
```

## Architecture Overview

This is a 2025 FRC Everybot robot project using WPILib's command-based framework with YAGSL (Yet Another Generic Swerve Library) for swerve drive control.

### Key Libraries
- **YAGSL** (`swervelib.*`): Handles swerve drive configuration and control. Config files are in `src/main/deploy/swerve/neo/`
- **PathPlanner**: Autonomous path planning and following. Paths/autos are in `src/main/deploy/pathplanner/`
- **REVLib**: REV Robotics motor controller support (NEO motors)

### Code Structure

**Main Entry Points:**
- `Robot.java` - TimedRobot lifecycle, starts RobotContainer
- `RobotContainer.java` - Subsystem instantiation, controller bindings, auto chooser setup

**Subsystems** (`subsystems/`):
- `SwerveSubsystem` - YAGSL-based swerve drive with PathPlanner integration
- `RollerSubsystem` - Intake/outtake rollers for coral and algae
- `ArmSubsystem` - Arm positioning mechanism
- `ClimberSubsystem` - Climbing mechanism

**Commands** (`commands/`):
- Roller commands: `AlgieInCommand`, `AlgieOutCommand`, `CoralOutCommand`, `CoralStackCommand`
- Arm commands: `ArmUpCommand`, `ArmDownCommand`
- Climber commands: `ClimberUpCommand`, `ClimberDownCommand`

**Configuration:**
- `Constants.java` - Motor IDs, speeds, current limits organized by subsystem
- `Bindings.java` - Swerve drive input stream configuration and simulation setup
- `Autos.java` - PathPlanner auto registration and chooser

### Controller Layout
- Port 0: Driver controller (driving + some mechanisms)
- Port 1: Operator controller (arm, climber)
- Port 2: God controller (duplicate of driver + operator bindings)

### Swerve Configuration
YAGSL config files in `src/main/deploy/swerve/neo/`:
- `swervedrive.json` - Main config with NavX IMU
- `modules/*.json` - Individual module configs (frontleft, frontright, backleft, backright)
- Uses NEO motors with absolute encoders

### Autonomous
PathPlanner autos are registered in `Autos.java`. Named commands (like `coralOut`) must be registered before building autos. Auto selection is via SmartDashboard chooser.
