package cbccore.low.controllers;

public class ServoController {
	private int port;
	public ServoController(int port) throws InvalidPortException {
		if(port < 0 || port > 4)
		{
			throw new InvalidPortException();
		}
		this.port = port;
	}
	public void setPosition(int pos)
	{
		CBC.servo.set_servo_position(this.port, pos);
	}
	public int getPosition()
	{
		return CBC.servo.get_servo_position(this.port);
	}
	public void enableServos()
	{
		CBC.servo.enable_servos();
	}
	public void disableServos()
	{
		CBC.servo.disable_servos();
	}
}