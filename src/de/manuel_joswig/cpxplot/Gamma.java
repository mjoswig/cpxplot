package de.manuel_joswig.cpxplot;

import java.util.*;
import org.nfunk.jep.*;
import org.nfunk.jep.function.*;
import org.nfunk.jep.type.Complex;

/**
 * Gamma function for JEP
 * 
 * @author		Manuel Joswig
 * @copyright	2012 Manuel Joswig
 */
public class Gamma extends PostfixMathCommand {
	private final Complex E = new Complex(Math.E, 0);
	
	public Gamma() {
		numberOfParameters = 1;
	}
	
	public void run(Stack inStack) throws ParseException {
		checkStack(inStack);
		Object param = inStack.pop();
		
		if (param instanceof Complex) {
			Complex result = new Complex(0, 0);
			
			inStack.push(new Complex(result));
		}
		else {
			throw new ParseException("Invalid parameter type!");
		}
	}
}
