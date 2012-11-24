
import org.jruby.embed.ScriptingContainer;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class test{
	
	public static ScriptingContainer man;
	public static void init(){
		man = new ScriptingContainer();
	}


	public static void main(String[] ar){
		try{
			init();
/*			ScriptingContainer man = new ScriptingContainer();
			man.runScriptlet("puts \"Halo!, I'm Lindsay Lohan\"");
*/			//Simple call example
			ruby_out("Hallo, I'm Lindsay Lohan.");
			load_and_run_ruby_file("rubyinput.rb");
			//Load a ruby script example
			Object receiver = load_ruby_file("ruby-method.rb");
			//SETUP THE VARIABLE
			//man.put("$input_variable", "Hallo, I'm Lindsay being shared, like a whore.");
			boolean t = man.callMethod(receiver, "print_output", "Hallo I'm a whore", Boolean.class);
			System.out.println("My output is " + t);

		}catch(Exception t){
			t.printStackTrace(System.out);
		}
	}

	//Utility Methods
	//simple output		
	public static void ruby_out(String outputS){
		man.runScriptlet("puts \"" + outputS + "\"");
	}

	//load scriptlet file
	public static Object load_ruby_file(String input_path) throws Exception{
		return man.runScriptlet(new FileReader(input_path), input_path );
	}

	public static void load_and_run_ruby_file(String input_path) throws Exception{
		man.runScriptlet(new FileReader(input_path), input_path );
	}

}
