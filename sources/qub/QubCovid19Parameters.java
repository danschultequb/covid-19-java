package qub;

/**
 * Application parameters for the qub/covid-19 CLI application.
 */
public class QubCovid19Parameters
{
    private final CharacterWriteStream output;
    private final VerboseCharacterWriteStream verbose;
    private final Covid19DataSource dataSource;

    /**
     * Create a new application parameters object.
     * @param output The output stream of the application.
     * @param dataSource The source of data about the Covid-19 pandemic.
     */
    public QubCovid19Parameters(CharacterWriteStream output, VerboseCharacterWriteStream verbose, Covid19DataSource dataSource)
    {
        PreCondition.assertNotNull(output, "output");
        PreCondition.assertNotNull(verbose, "verbose");
        PreCondition.assertNotNull(dataSource, "dataSource");

        this.output = output;
        this.verbose = verbose;
        this.dataSource = dataSource;
    }

    /**
     * Get the output stream of the application.
     * @return The output stream of the application.
     */
    public CharacterWriteStream getOutput()
    {
        return this.output;
    }

    /**
     * Get the verbose stream of the application.
     * @return The verbose stream of the application.
     */
    public VerboseCharacterWriteStream getVerbose()
    {
        return this.verbose;
    }

    /**
     * Get the source of data about the Covid-19 pandemic.
     * @return The source of data about the Covid-19 pandemic.
     */
    public Covid19DataSource getDataSource()
    {
        return this.dataSource;
    }
}
