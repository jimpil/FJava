
public class Greeter implements IFunction {
	
	public String greet(String s){
		return "Hi " + s + "...How are you?";
	}
	public String greetAll(String p1, String p2){
		return "Hey guys!What's happening?" + "\n" + greet(p1) + "\n" + greet(p2);
	}
	
	@Override
	public Object invoke(Object o) {
		return greet((String) o);	
	 }
	@Override
	public Object invoke(Object o1, Object o2) {
		return greetAll((String)o1, (String)o2);				
	   }  
}
