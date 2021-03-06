package hu.bme.mit.yakindu.analysis.workhere;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.junit.Test;
import org.yakindu.sct.model.sgraph.State;
import org.yakindu.sct.model.sgraph.Statechart;

import hu.bme.mit.model2gml.Model2GML;
import hu.bme.mit.yakindu.analysis.modelmanager.ModelManager;
import org.yakindu.sct.model.sgraph.Transition;
import org.yakindu.sct.model.stext.stext.impl.EventDefinitionImpl;
import org.yakindu.sct.model.stext.stext.impl.VariableDefinitionImpl;

public class Main {
	@Test
	public void test() {
		main(new String[0]);
	}
	
	public static void main(String[] args) {
		ModelManager manager = new ModelManager();
		Model2GML model2gml = new Model2GML();
		
		List<String> vars = new ArrayList<String>();
		List<String> events = new ArrayList<String>();

		
		// Loading model
		EObject root = manager.loadModel("model_input/example.sct");
		
		// Reading model
		Statechart s = (Statechart) root;
		TreeIterator<EObject> iterator = s.eAllContents();
		int nameIterator = 1;
		while (iterator.hasNext()) {
			EObject content = iterator.next();
			if(content instanceof State) {
				State state = (State) content;
				for (Transition trans : state.getOutgoingTransitions()) {
 					State next = (State) trans.getTarget();
 					System.out.println(state.getName() + " -> " + next.getName());
 				}
				
				if(state.getOutgoingTransitions().isEmpty()) {
					System.out.println("It's a trap" + state.getName());
				}
				
				if(state.getName().isEmpty()) {
					System.out.println("State has non name, it could be: state_" + nameIterator);
				}
					
			}
			else if (content instanceof EventDefinitionImpl) {
				EventDefinitionImpl eventDef = (EventDefinitionImpl) content;
				System.out.println("Event: " + eventDef.getName());
				events.add(eventDef.getName());
			}
			else if (content instanceof VariableDefinitionImpl) {
				VariableDefinitionImpl variableDef = (VariableDefinitionImpl) content;
				System.out.println("Variable: " + variableDef.getName());
				vars.add(variableDef.getName());
			}
		}
		System.out.println(codegen2(events, vars));
		
		
		// Transforming the model into a graph representation
		String content = model2gml.transform(root);
		// and saving it
		manager.saveFile("model_output/graph.gml", content);
	}
	
	public static String codegen1(List<String> vars) {
		String out = "";
		out += "\tpublic static void print(IExampleStatemachine s) {\n";
		for (int i = 0; i < vars.size(); i++) {
			out += "\t\tSystem.out.println(\"" + vars.get(i).charAt(0) + "= \" + s.getSCInterface().get" + vars.get(i) + "());\n";
		}
		out += "\t}";
		return out;
	}
	
	public static String codegen2(List<String> events,  List<String> vars) {
		String out = "";
		out += "public class RunStatechart {	\n";
		out += "	public static void main(String[] args) throws IOException {\n";
		out += "		ExampleStatemachine s = new ExampleStatemachine();\n";
		out += "		s.setTimer(new TimerService());\n";
		out += "		RuntimeService.getInstance().registerStatemachine(s, 200);\n";
		out += "		String input = \"\";\n";
		out += "		BufferedReader buffrdr = new BufferedReader(new InputStreamReader(System.in));\n";
		out += "		boolean run = true;\n";
		out += "		s.init();\n";
		out += "		s.enter();\n";
		out += "		while(run) {\n";
		out += "			input = buffrdr.readLine();\n";
		out += "			switch (input) {\n";
		out += "				case \"start\":\n";
		out += "					s.raiseStart();\n";
		out += "					s.runCycle();\n";
		out += "					print(s);\n";
		out += "					break;\n";
		for (int i = 0; i < events.size(); i++) {
			out += "\t\t\t\tcase \"" + events.get(i) + "\":\n";
			out += "\t\t\t\t\ts.raise" + events.get(i) + "();\n";
			out += "\t\t\t\t\tts.runCycle();\n";
			out += "\t\t\t\t\tprint(s);\n";
			out += "\t\t\t\t\tbreak;\n";
		}
		
		out += "				case \"exit\":\n";
		out += "					run = false;\n";
		out += "					print(s);\n";
		out += "					break;\n";
		out += "				default:\n";
		out += "					System.out.println(\"Invalid command, add a valid command!\");\n";
		out += "		}\n";
		out += "	}\n";
		out += "	buffrdr.close();\n";
		out += "	System.exit(0);\n";
		out += "}\n";
		
		 
		out += "\tpublic static void print(IExampleStatemachine s) {\n";
		for (int i = 0; i < vars.size(); i++) {
			out += "\t\tSystem.out.println(\"" + vars.get(i).charAt(0) + "= \" + s.getSCInterface().get" + vars.get(i) + "());\n";
		}
		out += "\t}";
		return out;
	}
	
	
	
}
