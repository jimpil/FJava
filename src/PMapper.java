
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future; 

public class PMapper { //thread-safe PMapper object
	private final ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

	public Collection<Object> map(final IFunction fn, Collection<Object> coll){
	
		Collection<Future<Object>> futureOutcomes = new ArrayList<Future<Object>>();
		CompletionService<Object> pool = new ExecutorCompletionService<Object>(service);
		
		for(final Object x : coll){
			Future<Object> task = pool.submit(new Callable<Object>(){
				@Override
				public Object call() throws Exception {
					return fn.invoke(x);
				}
			});		
			futureOutcomes.add(task);	
		}

		Collection<Object> fres = new ArrayList<Object>();
		for(int i = 0; i < futureOutcomes.size(); i++){
			   try {
				    fres.add(pool.take().get());
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
	   
		}
			
		service.shutdown();
		return fres;	
	}
	
	public Collection<Object> map(final IFunction fn, final Collection<Object> coll1, final Collection<Object> coll2){
		int c1s = coll1.size(); 
		int c2s = coll2.size();
		int sizeToUse =  (c1s <= c2s) ? c1s : c2s; //the smallest collection will do (just like Clojure)
		
		Collection<Future<Object>> futureOutcomes = new ArrayList<Future<Object>>();
		CompletionService<Object> pool = new ExecutorCompletionService<Object>(service);
		
		for(int i=0; i < sizeToUse; i++){
			final int t = i;
			Future<Object> task = pool.submit(new Callable<Object>(){
				@Override
				public Object call() throws Exception {
					return fn.invoke((((List<Object>) coll1).get(t)), 
				                     (((List<Object>) coll2).get(t)));
				}
			});		
			futureOutcomes.add(task);	
		}

		Collection<Object> fres = new ArrayList<Object>();
		for(int i = 0; i < futureOutcomes.size(); i++){
			   try {
				    fres.add(pool.take().get());
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
	   
		}
			
		service.shutdown(); //shutdown gracefully
		return fres;		
	}
	
//----------------------------------------------------------------------------------------------------	
//some testing follows
		public static void main(String... args){
			PMapper mapper = new PMapper();
			Collection<Object> coll = new ArrayList<Object>();
			for(int i=0;i<20;i++) 
				coll.add(i);  //dummy data
			
			Collection<?> result = mapper.map(new IFunction(){//a HEAVY incrementer function object
				private Number inc(int x){
					return x + 1;
				}
				@Override
				public Object invoke(Object o) {
					try { 
						System.out.println("Sleeping from thread: " + Thread.currentThread().getId());
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}			
				    return inc((int)o);
				 }
				@Override
				public Object invoke(Object o1, Object o2) {
					throw new UnsupportedOperationException("Cannot increment 2 numbers at the same time!");
				   }  
				}, coll);
			
			System.out.println(result);
       }
}
