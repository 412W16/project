import java.awt.Point;
import java.util.Stack;

public class Tower {

	private Stack rings = new Stack();
	private String number;
	private int height;
	
	public Tower(String number, int height) {
		this.number = number;
		this.height = height;
	}
	
	public Stack getRings() {
		return this.rings;
	}
	
	public void addRing(Ring r) {
		rings.push(r);
		this.height += 1;
	}
	
	public Ring removeRing() {
		this.height -= 1;
		return (Ring) rings.pop();
	}
	
	public String getNumber() {
		return this.number;
	}
	
	public int getHeight() {
		return this.height;
	}
}
