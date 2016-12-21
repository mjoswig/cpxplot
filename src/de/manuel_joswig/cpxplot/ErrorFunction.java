package de.manuel_joswig.cpxplot;
import java.util.*;
import org.nfunk.jep.*;
import org.nfunk.jep.function.*;
import org.nfunk.jep.type.Complex;

/**
 * Error function for JEP
 * 
 * @author		Manuel Joswig
 * @copyright	2012 Manuel Joswig
 */
public class ErrorFunction extends PostfixMathCommand {
	public ErrorFunction() {
		numberOfParameters = 1;
	}
	
	private long factorial(double n) {
		long x = 1;
		
		for (int i = 1; i <= n; i++) {
			x *= i;
		}
		
		return x;
	}
	
	public void run(Stack inStack) throws ParseException {
		checkStack(inStack);
		Object param = inStack.pop();
		
		if (param instanceof Complex) {
			int n = 0;
			
			Complex a = (Complex) param;
			Complex s = (Complex) param;
			Complex l = new Complex(0, 0);
			Complex z2 = (((Complex) param).neg()).mul((Complex) param);
			
			while (!l.equals(s) && n < 80) {
				l = s;
				n++;
				
				a = a.mul(z2.div(new Complex(n, 0)));
				s = s.add(a.div(new Complex((n << 1) + 1, 0)));
			}
			
			Complex result = s;
			
			inStack.push(new Complex(result));
		}
		else {
			throw new ParseException("Invalid parameter type!");
		}
	}
}
