
import org.jruby.embed.ScriptingContainer;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.File;
import com.ibcinc.development.utilities.file.FileUtility;
import java.util.ArrayList;
import java.util.HashMap;

public class testprocessing{
	
	public static FileUtility input;
	public static ScriptingContainer man;
	public static HashMap<String, String> rubyTester;
	public static boolean debugMode;
	
	public static void init(){
		man = new ScriptingContainer();
		rubyTester = new HashMap<String, String>();
		rubyTester.put("email", "ruby-man/email_line_tester.rb");
		rubyTester.put("manifest", "ruby-man/manifest_line_tester.rb");
		rubyTester.put("shipment", "ruby-man/shipment_line_tester.rb");
	}

	public static void main(String[] ar){
		int line_counter = 0;
		try{

			if( ar.length == 0 )
				throw new Exception("Prorgram Usage: testprocessing pathToIBCInputFile [-d]");
			
			debugMode = find_variable(ar, "-d");
			String file_name = find_file_name(ar);
			
			init();
			input = new FileUtility( new File(file_name) );
			ArrayList<String> inputFile = input.getDataContentsFromFile();
			
			boolean initEmailObject = true;
			boolean manObjectTest = false;
			boolean initShipmentObject = true;
			if(debugMode)
				System.out.println("The Input file is: "+ file_name);
			Object receiver = null;
			for(String s : inputFile){
				line_counter++;

				//Depending on how the line starts test the input of the file
				if(s.startsWith("6") || s.startsWith("7")){
					if(initEmailObject){
						receiver = load_ruby_file(rubyTester.get("email"));
						initEmailObject = false;
					}
					man.put("$debug", debugMode);
					boolean t = man.callMethod(receiver, "test_email_line", s, Boolean.class);
					if(!t){
						throw new Exception("Email Line Test Failed. Line# " + line_counter );
					}else{
						if(debugMode)
							System.out.println("Email line is OK");
					}
				}else if(s.startsWith("1")){
					if(manObjectTest)
						throw new Exception("Multiple Manifest lines detected. Line# " + line_counter );
					
					receiver = load_ruby_file(rubyTester.get("manifest"));
					man.put("$debug", debugMode);
					boolean t = man.callMethod(receiver, "test_manifest_line", s, Boolean.class);
					if(!t){
						throw new Exception("Manifest Line Test Failed. Line# " + line_counter );
					}else{
						if(debugMode)
							System.out.println("Manifest Line is OK");
					}
					manObjectTest = true;
				}else if(s.startsWith("8") || s.startsWith("9")){
					
					if(initShipmentObject){
						receiver = load_ruby_file(rubyTester.get("shipment"));
						initShipmentObject = false;
					}
					man.put("$debug", debugMode);
					boolean t = man.callMethod(receiver, "test_shipment_line", s, Boolean.class);
					if(!t){
						throw new Exception("Shipment Line Test Failed. Line# " + line_counter );
					}else{
						if(debugMode)
							System.out.println("Shipment line is OK");
					}
				
				}else{
					throw new Exception("Input in file "+ file_name + " is not supported: " + s );
				}
				
			}

			if(initEmailObject){
				throw new Exception("No Email Lines detected in file " + file_name);
			}
			if(!manObjectTest){
				throw new Exception("No Manifest line detected in file " + file_name);
			}
			if(initShipmentObject){
				throw new Exception("No shipment lines of type 8-9, detected in file " + file_name);
			}
			System.out.println("Congratulations: All tests passed on file: " + file_name);
			
		}catch(Exception t){
			t.printStackTrace(System.out);
		}
	}

	public static boolean find_variable(String[] ar, String search_input){
		boolean found = false;
		for(String s : ar){
			found = s.equalsIgnoreCase(search_input);
			if(found)
				break;
		}
		return found;
	}
	
	public static String find_file_name(String[] ar) throws Exception{
		if( debugMode && ar.length == 1 )
			throw new Exception("Prorgram Usage: testprocessing pathToIBCInputFile [-d]");
		String str = "";
		int i = -1;
		for(String s: ar){
			if( ! ar[++i].startsWith("-") ){
				return s;
			}
		}
		return str;
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
