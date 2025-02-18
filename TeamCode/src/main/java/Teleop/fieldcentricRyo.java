package Teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@TeleOp
public class fieldcentricRyo extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        // Declare our motors
        // Make sure your ID's match your configuration
        DcMotor fl = hardwareMap.dcMotor.get("fl");
        DcMotor bl = hardwareMap.dcMotor.get("bl");
        DcMotor fr = hardwareMap.dcMotor.get("fr");
        DcMotor br = hardwareMap.dcMotor.get("br");
        DcMotor leftHang = hardwareMap.dcMotor.get("leftHang");
        DcMotor rightHang = hardwareMap.dcMotor.get("rightHang");
        DcMotor leftLift = hardwareMap.dcMotor.get("LL");
        DcMotor rightLift = hardwareMap.dcMotor.get("RL");
        Servo claw = hardwareMap.servo.get("claw");
        Servo leftElbow = hardwareMap.servo.get("leftElbow");
        Servo rightElbow = hardwareMap.servo.get("rightElbow");

        leftLift.setDirection(DcMotorSimple.Direction.FORWARD);
        leftLift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        rightLift.setDirection(DcMotorSimple.Direction.FORWARD);
        rightLift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Reverse the right side motors. This may be wrong for your setup.
        // If your robot moves backwards when commanded to go forwards,
        // reverse the left side instead.
        // See the note about this earlier on this page.
        fl.setDirection(DcMotor.Direction.REVERSE);
        fr.setDirection(DcMotor.Direction.FORWARD);
        bl.setDirection(DcMotor.Direction.REVERSE);
        br.setDirection(DcMotor.Direction.FORWARD);

        rightLift.setDirection(DcMotorSimple.Direction.FORWARD);
        leftLift.setDirection(DcMotorSimple.Direction.FORWARD);

        // Retrieve the IMU from the hardware map
        IMU imu = hardwareMap.get(IMU.class, "imu");
        // Adjust the orientation parameters to match your robot
        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.FORWARD,
                RevHubOrientationOnRobot.UsbFacingDirection.UP));
        // Without this, the REV Hub's orientation is assumed to be logo up / USB forward
        imu.initialize(parameters);

        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {
            /*double y = -gamepad1.left_stick_y; // Remember, Y stick value is reversed
            double x = gamepad1.left_stick_x;
            double rx = gamepad1.right_stick_x;
            */
            double y = Math.abs(gamepad1.left_stick_y) > 0.1 ? -gamepad1.left_stick_y : 0;
            double x = Math.abs(gamepad1.left_stick_x) > 0.1 ? gamepad1.left_stick_x : 0;
            double rx = Math.abs(gamepad1.right_stick_x) > 0.1 ? gamepad1.right_stick_x : 0;

            // This button choice was made so that it is hard to hit on accident,
            // it can be freely changed based on preference.
            // The equivalent button is start on Xbox-style controllers.
            if (gamepad1.options) {
                imu.resetYaw();
            }
           /* telemetry.addData("Yaw (Radians)", imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS));
            telemetry.update();
            */

            telemetry.addData("IMU Yaw (Degrees)", imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES));
            telemetry.update();

            double botHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

            // Rotate the movement direction counter to the bot's rotation
            double rotX = x * Math.cos(botHeading) - y * Math.sin(botHeading);
            double rotY = x * Math.sin(botHeading) + y * Math.cos(botHeading);

            // rotX = rotX * 1.1;  // Counteract imperfect strafing

            rotX *= 1.1;  // Counteract imperfect strafing
            // Denominator is the largest motor power (absolute value) or 1
            // This ensures all the powers maintain the same ratio,
            // but only if at least one is out of the range [-1, 1]
            double denominator = Math.max(Math.abs(rotY) + Math.abs(rotX) + Math.abs(rx), 1);
            double frontLeftPower = (rotY - rotX - rx) / denominator; //not
            double backLeftPower = (rotY - rotX - rx) / denominator;
            double frontRightPower = (rotY + rotX + rx) / denominator; //not
            double backRightPower = (rotY - rotX + rx) / denominator;

            fl.setPower(frontLeftPower);
            bl.setPower(backLeftPower);
            fr.setPower(frontRightPower);
            br.setPower(backRightPower);

            telemetry.addData("IMU Heading", botHeading);
            telemetry.addData("Input X", x);
            telemetry.addData("Input Y", y);
            telemetry.addData("Rotated X", rotX);
            telemetry.addData("Rotated Y", rotY);
            telemetry.addData("FL Power", frontLeftPower);
            telemetry.addData("BL Power", backLeftPower);
            telemetry.addData("FR Power", frontRightPower);
            telemetry.addData("BR Power", backRightPower);
            telemetry.update();

            //Added TeleOp into the field centric if it doesn't work we will go back to og (I changed my mind)
            //Front back Left
            /*if (Math.abs(gamepad1.left_stick_y) > .2) {
                fl.setPower(gamepad1.left_stick_y * 1);
                bl.setPower(gamepad1.left_stick_y * -1);
            } else {
                fl.setPower(0);
                bl.setPower(0);
            }

            //Front back Right
            if (Math.abs(gamepad1.right_stick_y) > .2) {
                fr.setPower(gamepad1.right_stick_y * -1);
                br.setPower(gamepad1.right_stick_y * 1);
            } else {
                fr.setPower(0);
                br.setPower(0);
            }
           */
            //up and down p2
            //Lift
            if (gamepad2.left_bumper) {
                rightLift.setPower(-.5);
                leftLift.setPower(-.5);

            } else if (gamepad2.right_bumper) {
                rightLift.setPower(0.9);
                leftLift.setPower(0.9);
            } else {
                rightLift.setPower(0);
                leftLift.setPower(0);
            }

            // Moves the Elbow in order of up, middle, and down
            if (gamepad2.b) {
                leftElbow.setPosition(1); // always goes down in values
                rightElbow.setPosition(0); // always goes up in values

            } else if (gamepad2.a) {
                leftElbow.setPosition(.30);   // sigma sigma on the wall whos the skibidiest of them all
                rightElbow.setPosition(.30);

            } else if (gamepad2.x) {
                leftElbow.setPosition(.50);
                rightElbow.setPosition(.50);

            }
            // Hang to go up and down
            if (gamepad2.dpad_up) {
                leftHang.setPower(.9);
                rightHang.setPower(.9);

            } else if (gamepad2.dpad_down) {
                leftHang.setPower(-.9);
                rightHang.setPower(-.9);

            } else {
                leftHang.setPower(0);
                rightHang.setPower(0);
            }
            //Claw snap snap
            if (gamepad2.dpad_right) { //open?
                claw.setPosition(1);

            } else if (gamepad2.dpad_left) { //colsed?????
                claw.setPosition(0.20);
            }
        }
    }
}