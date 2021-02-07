package qub;

/**
 * Parameters for the "show" action in the qub/covid-19 CLI application.
 */
public class QubCovid19ShowParameters
{
    private final CharacterToByteWriteStream output;
    private final VerboseCharacterToByteWriteStream verbose;
    private final Folder dataFolder;
    private final Covid19DataSource dataSource;

    /**
     * Create a new application parameters object.
     * @param output The output stream of the application.
     * @param dataSource The source of data about the Covid-19 pandemic.
     */
    public QubCovid19ShowParameters(CharacterToByteWriteStream output, VerboseCharacterToByteWriteStream verbose, Folder dataFolder, Covid19DataSource dataSource)
    {
        PreCondition.assertNotNull(output, "output");
        PreCondition.assertNotNull(verbose, "verbose");
        PreCondition.assertNotNull(dataFolder, "dataFolder");
        PreCondition.assertNotNull(dataSource, "dataSource");

        this.output = output;
        this.verbose = verbose;
        this.dataFolder = dataFolder;
        this.dataSource = dataSource;
    }

    /**
     * Get the output stream of the application.
     * @return The output stream of the application.
     */
    public CharacterToByteWriteStream getOutput()
    {
        return this.output;
    }

    /**
     * Get the verbose stream of the application.
     * @return The verbose stream of the application.
     */
    public VerboseCharacterToByteWriteStream getVerbose()
    {
        return this.verbose;
    }

    public Folder getDataFolder()
    {
        return this.dataFolder;
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
