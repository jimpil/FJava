import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Mapper { //thread-safe Mapper object

	public Collection<Object> map(IFunction fn, Collection<Object> coll){
		
		Collection<Object> outcome = new ArrayList<Object>();
		for(Object x : coll)
			outcome.add(fn.invoke(x));
		
		return outcome;	
	}
	
	public Collection<Object> map(IFunction fn, Collection<Object> coll1, Collection<Object> coll2){
		int c1s = coll1.size(); 
		int c2s = coll2.size();
		int sizeToUse =  (c1s <= c2s) ? c1s : c2s; //the smallest collection will do (just like Clojure)
		
		Collection<Object> outcome = new ArrayList<Object>();		
		for(int i=0; i < sizeToUse; i++)
			outcome.add(fn.invoke((((List<Object>) coll1).get(i)), 
					              (((List<Object>) coll2).get(i))));
			
		return outcome;	
	}
//----------------------------------------------------------------------------------------------------	
//some testing follows
	public static void main(String... args){
		Mapper mapper = new Mapper();
		Collection<Object> coll = new ArrayList<Object>();
		for(int i=0;i<20;i++) 
			coll.add(i);  //dummy data
		
		Collection<?> result = mapper.map(new IFunction(){//an incrementer function object
			private Number inc(int x){
				return x + 1;
			}
			@Override
			public Object invoke(Object o) {
					return inc((int)o);
			 }
			@Override
			public Object invoke(Object o1, Object o2) {
				throw new UnsupportedOperationException("Cannot increment 2 numbers at the same time!");
			   }  
			}, coll);
		
		System.out.println(result);
		
//testing 2-arg version
		Collection<Object> temp = new ArrayList<Object>();
		for(int i=20;i<40;i++) //make this larger to trigger the exception
			temp.add(i);  //dummy data
		
		//now we have 2 collections coll and temp
		Collection<?> result2 = mapper.map(new IFunction(){ //an adder function-object
			public Number add(int x, int y){
				return x + y;
			}
			@Override
			public Object invoke(Object o) { //addition with 1 argument returns that argument
				return o;	
			 }
			@Override
			public Object invoke(Object o1, Object o2) {
				return add((int)o1, (int)o2);				
			   }
  
			}, coll, temp);//the 2 collections we're mapping across
		
		System.out.println(result2);
	}
	

}
