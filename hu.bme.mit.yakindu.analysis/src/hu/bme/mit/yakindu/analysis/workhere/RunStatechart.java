package hu.bme.mit.yakindu.analysis.workhere;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import hu.bme.mit.yakindu.analysis.RuntimeService;
import hu.bme.mit.yakindu.analysis.TimerService;
import hu.bme.mit.yakindu.analysis.example.ExampleStatemachine;
import hu.bme.mit.yakindu.analysis.example.IExampleStatemachine;


public class RunStatechart {
	
	public static void main(String[] args) throws IOException {
		ExampleStatemachine s = new ExampleStatemachine();
		s.setTimer(new TimerService());
		RuntimeService.getInstance().registerStatemachine(s, 200);
		String input = "";
		BufferedReader buffrdr = new BufferedReader(new InputStreamReader(System.in));
		boolean run = true;
		s.init();
		s.enter();
		while(run) {
			input = buffrdr.readLine();
			switch (input) {
				case "start":
					s.raiseStart();
					s.runCycle();
					print(s);
					break;
				case "black":
					s.raiseBlack();
					s.runCycle();
					print(s);
					break;
				case "white":
					s.raiseWhite();
					s.runCycle();
					print(s);
					break;
				case "exit":
					run = false;
					print(s);
					break;
				default:
					System.out.println("Invalid command, add a valid command!");
		}
	}
	buffrdr.close();
	System.exit(0);
}

	public static void print(IExampleStatemachine s) {
		System.out.println("W = " + s.getSCInterface().getWhiteTime());
		System.out.println("B = " + s.getSCInterface().getBlackTime());
	}
}
