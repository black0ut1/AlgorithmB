package cz.zcu.pperncka.io.args;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

/**
 * Class representing validation of positive integer
 * that is passed as command line argument.
 */
public class PositiveIntValidator implements IParameterValidator {
	
	@Override
	public void validate(String name, String value) throws ParameterException {
		String message = "Value of parameter " + name + " must be an integer bigger than 0: \"" + value + "\"";
		
		int n;
		try {
			n = Integer.parseInt(value);
		} catch (NumberFormatException e) {
			throw new ParameterException(message);
		}
		
		if (n <= 0)
			throw new ParameterException(message);
	}
}
