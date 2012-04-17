/*
 * This file is part of CBCJVM.
 * CBCJVM is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * CBCJVM is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with CBCJVM.  If not, see <http://www.gnu.org/licenses/>.
 */

package cbccore.create;

import java.io.IOException;

import cbccore.Device;
import cbccore.create.commands.*;

/**
 * Allows high-level access the the create (vs. cbccore.low.Create)
 * 
 * @see cbccore.low.Create
 * @see cbccore.movement.plugins.create.CreateMovementPlugin
 * @see cbccore.movement.DriveTrain
 */
public class Create {
	
	public enum Mode {
		Safe, Passive, Full, Off
	}
	
	private float lag;
	
	private static cbccore.low.Create lowCreate =
		Device.getLowCreateController();
	private static LowSideDrivers lowSideDrivers =
		new LowSideDrivers(lowCreate);
	
	public Create() throws CreateConnectException {
		lag=0;		//TODO:Find good 'lag' value. I don't have any cbc/create 
				//	to test it on at time of writing
		connect();
	}
	
	/**
	* Initalizes a new Create object with the lag value
	* 
	* @param lag How many seconds between sensor updates
	*/
	public Create(float lag) throws CreateConnectException{
		this.lag=lag;
		connect();
	}
	
	
	@Override
	public void finalize() {
		disconnect();
	}
	
	/**
	* Sets 'lag' value for create getter functions.
	*
	* @param lag How many seconds between sensor updates
	*/
	public void setLag(float lag){
		this.lag=lag;
	}
	
	public float getLag(){
		return lag;	
	}
	
	public void sendScript(Script script) {
		Script s = (Script) script.clone();
		for (Command c : s) {
			c.add(this);
		}
	}
	
	/**
	 * First step for connecting CBC to Create. This function puts the Create in
	 * the safe mode.
	 * 
	 * @see #disconnect
	 */
	public void connect() throws CreateConnectException {
		int ret = lowCreate.create_connect();
		if (ret < 0)
			throw new CreateConnectException();
	}

	/**
	 * Returns the Create to proper state. Call this at the end of your program.
	 * 
	 * @see #connect
	 */
	public void disconnect() {
		lowCreate.create_disconnect();
	}
	
	/**
	 * Puts the Create into passive mode (no motors).
	 * 
	 * @see Create.Mode
	 * @see #setMode
	 * @see #getMode
	 */
	public void start() {
		lowCreate.create_start();
	}
	
	/**
	 * Changes the Create's mode. Safe stops if it senses the drop sensor, Full
	 * will do everything ignoring safety triggers, even if it means destroying
	 * itself. o_O
	 * 
	 * @param m
	 *            Mode.Off, Mode.Passive, Mode.Safe, or Mode.Full
	 * @see Create.Mode
	 * @see #setMode
	 */
	public void setMode(Mode m) {
		if (m == Mode.Safe)
			lowCreate.create_safe();
		else if (m == Mode.Passive)
			lowCreate.create_passive();
		else if (m == Mode.Full)
			lowCreate.create_full();
		else
			lowCreate.create_safe();
	}
	
	/**
	 * Simulates a Roomba doing a spot clean.
	 * 
	 * @see #cover
	 * @see #demo
	 * @see #coverDock
	 */
	public void spot() {
		lowCreate.create_spot();
	}
	
	/**
	 * Simulates a Roomba covering a room.
	 * 
	 * @see #spot
	 * @see #demo
	 * @see #coverDock
	 */
	public void cover() {
		lowCreate.create_cover();
	}
	
	/**
	 * Runs a specified built-in demo (see Create IO documentation).
	 * 
	 * @param d
	 *            See Create IO documentation. I would normally look this up,
	 *            but it seems so pointless...
	 * @see #spot
	 * @see #cover
	 * @see #coverDock
	 */
	public void demo(int d) {
		lowCreate.create_demo(d);
	}
	
	/**
	 * The Create roams around until it sees an IR dock and then attempts to
	 * dock.
	 * 
	 * @see #spot
	 * @see #cover
	 * @see #demo
	 */
	public void coverDock() {
		lowCreate.create_cover_dock();
	}
	
	/**
	 * Returns the Create's mode.
	 * 
	 * @return Mode.Off, Mode.Passive, Mode.Safe, or Mode.Full
	 * @see Create.Mode
	 * @see #setMode
	 */
	public Mode getMode() {
		int m = lowCreate.get_create_mode(lag);
		if (m == 0)
			return Mode.Off;
		if (m == 1)
			return Mode.Passive;
		if (m == 2)
			return Mode.Safe;
		if (m == 3)
			return Mode.Full;
		return Mode.Off;
	}
	
	/**
	 * Stops the drive wheels
	 */
	public void stop() {
		lowCreate.create_stop();
	}
	
	/**
	 * Drives in an arc.
	 * 
	 * @param speed
	 *            range is 20-500mm/s
	 * @param radius
	 *            radius in mm/s.
	 *            <p>
	 *            A radius of 32767 will drive the robot straight.
	 *            <p>
	 *            A radius of 1 will spin the robot CCW
	 *            <p>
	 *            A radius of -1 will spin the robot CW
	 *            <p>
	 *            Negative radii will be right turns, positive radii left turns
	 * @see #driveStraight
	 */
	public void drive(int speed, int radius) {
		lowCreate.create_drive(speed, radius);
	}
	
	/**
	 * Drives straight at speed in mm/s
	 * 
	 * @param speed
	 *            20-500mm/s
	 */
	public void driveStraight(int speed) {
		lowCreate.create_drive_straight(speed);
	}
	
	/**
	 * Spins CW with edge speed of speed in mm/s
	 * 
	 * @param speed
	 *            20-500mm/s. Speed of edge (wheels) of bot.
	 * @see #spinCCW
	 * @see #spinBlock
	 */
	public void spinCW(int speed) {
		lowCreate.create_spin_CW(speed);
	}
	
	/**
	 * Spins CCW with edge speed of speed in mm/s
	 * 
	 * @param speed
	 *            20-500mm/s. Speed of edge (wheels) of bot.
	 * @see #spinCW
	 * @see #spinBlock
	 */
	public void spinCCW(int speed) {
		lowCreate.create_spin_CCW(speed);
	}
	
	/**
	 * Specifies individual left and right speeds in mm/s
	 * 
	 * @param r_speed
	 *            20-500mm/s. Speed of right wheel.
	 * @param l_speed
	 *            20-500mm/s. Speed of left wheel.
	 * @see #drive
	 */
	public void driveDirect(int r_speed, int l_speed) {
		lowCreate.create_drive_direct(r_speed, l_speed);
	}
	
	/**
	 * This function blocks and does a pretty accurate spin. Note that the
	 * function will not return until the spin is complete CAUTION: requesting
	 * the robot to spin more than about 3600 degrees may never terminate
	 * 
	 * @param speed
	 *            20-500mm/s. Speed of edge (wheels) of bot.
	 * @param angle
	 *            Angle in degrees to turn before returning.
	 *            <p>
	 *            <b>CAUTION: requesting the robot to spin more than about 3600
	 *            degrees may never terminate</b>
	 * @return -1 if error
	 * @see #spinCW
	 * @see #spinCCW
	 */
	public int spinBlock(int speed, int angle) {
		return lowCreate.create_spin_block(speed, angle);
	}
	
	// public int _get_raw_encoders(long* lenc, long* renc);
	
	/**
	 * Turn on/off the advance LED
	 * 
	 * @param on
	 *            1 to turn on light and 0 to turn it off
	 * @see #playLed
	 * @see #powerLed
	 */
	public void advanceLed(boolean on) {
		lowCreate.create_advance_led(on ? 1 : 0);
	}
	
	/**
	 * Turn on/off the play LED
	 * 
	 * @param on
	 *            1 to turn on light and 0 to turn it off
	 * @see #advanceLed
	 * @see #powerLed
	 */
	public void playLed(boolean on) {
		lowCreate.create_play_led(on ? 1 : 0);
	}
	
	/**
	 * Control the color and the brightness of the power LED
	 * 
	 * @param color
	 *            0 is red and 255 green
	 * @param brightness
	 *            0 is off and 255 is full brightness
	 * @see #advanceLed
	 * @see #playLed
	 */
	public void powerLed(int color, int brightness) {
		lowCreate.create_power_led(color, brightness);
	}
	
	/**
	 * This function sets the three digital out put pins 20,7,19 where 20 is the
	 * high bit and 19 is the low. You probably don't care about this function.
	 * 
	 * @param bits
	 *            Should have a value 0 to 7.
	 */
	public void digitalOutput(int bits) {
		lowCreate.create_digital_output(bits);
	}
	
	/**
	 * Turns on and off the signal for the three low side drivers (128 = 100%).
	 * A 0 or 1 should be given for each of the drivers to turn them off or on.
	 * You probably don't care about this function.
	 * 
	 * @return An instance of LowSideDrivers
	 */
	public LowSideDrivers getLowSideDrivers() {
		return lowSideDrivers;
	}
	
	/**
	 * This loads a song into the robot's memory. Song can be numbered 0 to 15.
	 * The first element in each row of the array should be the number of notes
	 * (1-16) the subsequent pairs of bytes should be tone and duration see the
	 * roomba SCI manual for note codes. User's program should load the song
	 * data into the array before calling this routine. Sets gc_song_array, an
	 * inaccessable variable. <b>DO NOT USE THIS FUNCTION UNTIL THE ISSUE IS
	 * RESOLVED</b>
	 * 
	 * @param num
	 *            Song can be numbered 0 to 15
	 * @see #playSong
	 */
	public void loadSong(int num) {
		lowCreate.create_load_song(num);
	}
	
	/**
	 * See the roomba SCI manual for note codes. Uses gc_song_array, an
	 * inaccessable variable. <b>DO NOT USE THIS FUNCTION UNTIL THE ISSUE IS
	 * RESOLVED</b>
	 * 
	 * @param num
	 *            Song can be numbered 0 to 15
	 * @see #loadSong
	 */
	public void playSong(int num) {
		lowCreate.create_play_song(num);
	}
	
	// public int read_block(char* data, int count);
	
	public int getDistance() {
		return lowCreate.get_create_distance(lag);
	}
	
	/**
	 * See Create IO Documentation. You probably don't care about this function.
	 * 
	 * @param write_byte
	 *            the byte to write
	 * @see #clearSerialBuffer
	 */
	public void writeByte(int write_byte) {
		char c = (char) write_byte;
		lowCreate.create_write_byte(c);
	}
	
	public void clearSerialBuffer() {
		lowCreate.create_clear_serial_buffer();
	}
	
	public static byte fromUnsigned(int c) {
		return (byte) ((c > 127) ? c - 256 : c);
	}
	
	public static int fromSigned(byte c) {
		return c & 0xFF;
	}
	
	/**
	 * Moves the create a set number of mm at a set speed. You should probably
	 * be using the movement library.
	 * 
	 * @param  mmps  The speed of the create in millimeters-per-second
	 * @param  mm    The number of mm for the create to move.
	 * @see #turnDeg
	 * @see cbccore.movement.DriveTrain
	 * @see cbccore.movement.plugins.create.CreateMovementPlugin
	 * @see cbccore.movement.DriveTrain#moveCm
	 */
	public void moveMm(int mmps, int mm) {
		double secs = mm/((double)mmps);
		driveStraight(mmps);
		try {
			//Thread.sleep(130); // what is this?
			Thread.sleep((int)(secs * 1000.));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		stop();
	}
	
	/**
	 * Moves the create a set number of degrees at a set speed. You should
	 * probably be using the movement library.
	 * 
	 * @param speed
	 *            The number of seconds to travel for. (shouldn't this be
	 *            changed to degrees-per-sec to make range checking easier on
	 *            the user?)
	 * @param deg
	 *            The number of degrees for the create to turn.
	 * @see #turnDeg
	 * @see cbccore.movement.DriveTrain
	 * @see cbccore.movement.plugins.create.CreateMovementPlugin
	 * @see cbccore.movement.DriveTrain#rotateDegrees
	 */
	public void turnDeg(int speed, int deg) {
		// deg = (int) ((double)deg * .85);
		if (deg < 0) {
			lowCreate.create_spin_CW(speed);
		} else {
			lowCreate.create_spin_CCW(speed);
		}
		lowCreate.create_spin_block(speed, deg);
		// shouldn't you stop it here? or does the create handle that for you?
	}
	
	public CliffState getCliffs() {
		byte buffer[] = new byte[12];
		writeByte(149);
		writeByte(8);
		writeByte(9);
		writeByte(10);
		writeByte(11);
		writeByte(12);
		writeByte(28);
		writeByte(29);
		writeByte(30);
		writeByte(31);
		lowCreate.create_read_block(buffer, buffer.length);
		int leftCliff = buffer[0];
		int leftFrontCliff = buffer[1];
		int rightFrontCliff = buffer[2];
		int rightCliff = buffer[3];
		int leftCliffAmount = fromSigned(buffer[4]) << 8;
		leftCliffAmount |= fromSigned(buffer[5]) << 0;
		int leftFrontCliffAmount = fromSigned(buffer[6]) << 8;
		leftFrontCliffAmount |= fromSigned(buffer[7]);
		int rightFrontCliffAmount = fromSigned(buffer[8]) << 8;
		rightFrontCliffAmount |= fromSigned(buffer[9]);
		int rightCliffAmount = fromSigned(buffer[10]) << 8;
		rightCliffAmount |= fromSigned(buffer[11]);
		
		return new CliffState(rightCliff, rightFrontCliff, leftCliff, leftFrontCliff, 
				rightCliffAmount, rightFrontCliffAmount, 
				leftCliffAmount, leftFrontCliffAmount);
	}
	
	private int gc_angle = 0;
	
	public int getAngle() {
		byte buffer[] = new byte[2];
			writeByte(142);
			writeByte(20);
			lowCreate.create_read_block(buffer, buffer.length);
			int newangle = fromSigned(buffer[0]) << 8;
			newangle |= fromSigned(buffer[1]);
			if(newangle > 32767){ //if this was meant to be a negative 16 bit int
				newangle=newangle - 65536;//convert from 16 bit 2's complement to 32bit int
			}
			gc_angle = (gc_angle + newangle) % 360;
			if(gc_angle < 0) gc_angle = gc_angle + 360;
			return gc_angle;
	}
	
	public boolean getLBump(float lag) {
			return lowCreate.get_create_lbump(lag) == 1;
	}
	
	public boolean getRBump(float lag) {
			return lowCreate.get_create_rbump(lag) == 1;
	}
	
	public boolean getLBump() {
			return lowCreate.get_create_lbump(this.lag) == 1;
	}
	
	public boolean getRBump() {
			return lowCreate.get_create_rbump(this.lag) == 1;
	}
}
