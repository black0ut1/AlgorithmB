package cz.zcu.pperncka.io.args;

import com.beust.jcommander.Parameter;

/** Class with atributes representing parsed command line arguments. */
public class Args {
	
	@Parameter(names = {"-net", "--network"},
			required = true,
			validateWith = FileExistsValidator.class,
			description = "Specifies a path to a .tntp file " +
					"containing the network.")
	public String networkFile;
	
	@Parameter(names = {"-odm", "--odmatrix"},
			required = true,
			validateWith = FileExistsValidator.class,
			description = "Specifies a path to a .tntp file " +
					"containing the origin-destination matrix.")
	public String matrixFile;
	
	@Parameter(names = {"-o", "--output"},
			required = true,
			description = "Specifies a path to a file to which the " +
					"resulting trips/flow of each arc will be written.")
	public String outputFile;
	
	@Parameter(names = {"-i", "--iterations"},
			required = true,
			validateWith = PositiveIntValidator.class,
			description = "Specifies a maximum number of iterations " +
					"the algorithm will make.")
	public int iterations;
	
	@Parameter(names = {"-rg", "--relative-gap"},
			description = "When specified, the relative gap will be " +
					"computed after each iteration and if it is lower " +
					"than the number specified by this parameter, the " +
					"algorithm will end before reaching max. iterations. " +
					"Beware that the calculation of relative gap is very " +
					"computationally demanding.")
	public double relativeGap;
	
	@Parameter(names = {"-t", "--threads"},
			validateWith = PositiveIntValidator.class,
			description = "When present, the parallelized version of " +
					"the algorithm will be executed. The number specifies " +
					"how many worker threads will be used. Note: launching " +
					"with -t 1 is not desirable since the program will take " +
					"longer than when launched without -t at all because " +
					"of additional overhead of the parallelized version.")
	public int threads;
	
	@Parameter(names = {"-h", "--help"},
			help = true,
			description = "Prints out this help.")
	public boolean help;
}
