
public interface IFunction {//limited to 2 arguments so far - Clojure goes up to 20 arguments
	Object invoke(Object o);
	Object invoke(Object o1, Object o2);

}
