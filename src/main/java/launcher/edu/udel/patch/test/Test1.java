package edu.udel.patch.test;

public class Test1 {

	private int x;
	
	public Test1() {
		x = 0;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public int getX() {
		return x;
	}
	
	public static void main(String[] args) {
		Test1 t = new Test1();
		System.out.println(t.getX()+"test1");
	}

}
