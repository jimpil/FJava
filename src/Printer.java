
public class Printer implements IFunction {
	
	public void pr(Object o){
		System.out.println(o);
	}
	
	@Override
	public Object invoke(Object o) {
		pr(o);
		return null; //printing returns nothing
	 }
	@Override
	public Object invoke(Object o1, Object o2) {
		pr(o1);
		pr(o2);
		pr("\n");
		return null; //printing returns nothing			
	   }
  

}
