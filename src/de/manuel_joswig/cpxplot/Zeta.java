package de.manuel_joswig.cpxplot;

import java.util.*;
import org.nfunk.jep.*;
import org.nfunk.jep.function.*;
import org.nfunk.jep.type.Complex;

/**
 * Zeta function for JEP
 * 
 * @author		Manuel Joswig
 * @copyright	2012 Manuel Joswig
 */
public class Zeta extends PostfixMathCommand {
	private final Complex E = new Complex(Math.E, 0);
	
	public Zeta() {
		numberOfParameters = 1;
	}
	
	public void run(Stack inStack) throws ParseException {
		checkStack(inStack);
		Object param = inStack.pop();
		
		if (param instanceof Complex) {
			Complex result = new Complex(0, 0);
			
			for (int i = 1; i <= 25; i++) {
				result = result.add(E.power((((new Complex(i, 0)).log()).mul(((Complex) param)))));
			}
			
			inStack.push(new Complex(result));
		}
		else {
			throw new ParseException("Invalid parameter type!");
		}
	}
}
