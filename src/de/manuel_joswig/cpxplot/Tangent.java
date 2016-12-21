package de.manuel_joswig.cpxplot;

import java.util.*;
import org.nfunk.jep.*;
import org.nfunk.jep.function.*;
import org.nfunk.jep.type.Complex;

/**
 * Fixed tangent function for JEP
 * 
 * @author		Manuel Joswig
 * @copyright	2012 Manuel Joswig
 */
public class Tangent extends PostfixMathCommand {
	public Tangent() {
		numberOfParameters = 1;
	}
	
	public void run(Stack inStack) throws ParseException {
		checkStack(inStack);
		Object param = inStack.pop();
		
		if (param instanceof Complex) {
			// tan(z) = sin(z) / cos(z)
			Complex result = (((Complex) param).sin()).div(((Complex) param).cos());
			
			inStack.push(new Complex(result));
		}
		else {
			throw new ParseException("Invalid parameter type!");
		}
	}
}
