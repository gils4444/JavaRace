package game.racers;

import java.util.Observable;

import game.arenas.Arena;
import utilities.EnumContainer;
import utilities.Fate;
import utilities.Mishap;
import utilities.Point;

public abstract class Racer extends Observable implements Runnable{
	protected static int lastSerialNumber = 1;

	private int serialNumber; // Each racer has an unique number, assigned by arena in addRacer() method
	private String name;
	private Point currentLocation;
	private Point finish;
	private Arena arena;
	private double maxSpeed;
	private double acceleration;
	private double currentSpeed;
	@SuppressWarnings("unused")
	private double failureProbability; // Chance to break down
	private EnumContainer.Color color; // (RED,GREEN,BLUE,BLACK,YELLOW)

	private Mishap mishap;

	/**
	 * @param name
	 * @param maxSpeed
	 * @param acceleration
	 * @param color
	 */
	public Racer(String name, double maxSpeed, double acceleration, utilities.EnumContainer.Color color) {
		this.serialNumber = Racer.lastSerialNumber++;
		this.name = name;
		this.maxSpeed = maxSpeed;
		this.acceleration = acceleration;
		this.color = color;
	}

	public abstract String className();

	public String describeRacer() {
		String s = "";
		s += "name: " + this.name + ", ";
		s += "SerialNumber: " + this.serialNumber + ", ";
		s += "maxSpeed: " + this.maxSpeed + ", ";
		s += "acceleration: " + this.acceleration + ", ";
		s += "color: " + this.color + ", ";
		s = s.substring(0, s.length() - 2);
		// returns a string representation of the racer, including: general attributes
		// (color name, number) and specific ones (numberOfWheels, etc.)
		s += this.describeSpecific();
		return s;
	}

	public abstract String describeSpecific();

	public int getSerialNumber() {
		return serialNumber;
	}

	private boolean hasMishap() {
		if (this.mishap != null && this.mishap.getTurnsToFix() == 0) {
			this.notifyObservers(EnumContainer.RacerEvent.REPAIRED);
			this.mishap = null;
		}
		return this.mishap != null;
	}

	public void initRace(Arena arena, Point start, Point finish) {
		this.arena = arena;
		this.currentLocation = new Point(start);
		this.finish = new Point(finish);
	}

	public void introduce() {
		// Prints a line, obtained from describeRacer(), with its type
		System.out.println("[" + this.className() + "] " + this.describeRacer());
	}

	public Point move(double friction) {
		double reductionFactor = 1;
		if (!(this.hasMishap()) && Fate.breakDown(failureProbability)) {
			this.mishap = Fate.generateMishap();
			if(this.mishap.isFixable())
				this.notifyObservers(EnumContainer.RacerEvent.BROKENDOWN);
			else
				this.notifyObservers(EnumContainer.RacerEvent.DISABLED);
			
			System.out.println(this.name + " Has a new mishap! (" + this.mishap + ")");
		}

		if (this.hasMishap()) {
			reductionFactor = mishap.getReductionFactor();
			this.mishap.nextTurn();
		}
		if (this.currentSpeed < this.maxSpeed) {
			this.currentSpeed += this.acceleration * friction * reductionFactor;
		}
		if (this.currentSpeed > this.maxSpeed) {
			this.currentSpeed = this.maxSpeed;
		}
		double newX = (this.currentLocation.getX() + (this.currentSpeed));
		Point newLocation = new Point(newX, this.currentLocation.getY());
		this.currentLocation = newLocation;

		if (this.currentLocation.getX() >= this.finish.getX()) {
			//this.arena.crossFinishLine(this);
			this.notifyObservers(EnumContainer.RacerEvent.FINISHED);
		}
		return this.currentLocation;

		// has a chance for failure ( see section ??.? )
		// returns new location
	 
	}
	
	public void run() {
		//while(arena.crossFinishLine(this)) {
			
		}
	}
}
