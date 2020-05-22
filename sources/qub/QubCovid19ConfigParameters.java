package qub;

/**
 * Parameters for the "config" action in the qub/covid-19 application.
 */
public class QubCovid19ConfigParameters
{
    private final CharacterWriteStream output;
    private final Folder dataFolder;
    private final DefaultApplicationLauncher defaultApplicationLauncher;

    public QubCovid19ConfigParameters(CharacterWriteStream output, Folder dataFolder, DefaultApplicationLauncher defaultApplicationLauncher)
    {
        PreCondition.assertNotNull(output, "output");
        PreCondition.assertNotNull(dataFolder, "dataFolder");
        PreCondition.assertNotNull(defaultApplicationLauncher, "defaultApplicationLauncher");

        this.output = output;
        this.dataFolder = dataFolder;
        this.defaultApplicationLauncher = defaultApplicationLauncher;
    }

    public CharacterWriteStream getOutput()
    {
        return this.output;
    }

    public Folder getDataFolder()
    {
        return this.dataFolder;
    }

    public DefaultApplicationLauncher getDefaultApplicationLauncher()
    {
        return this.defaultApplicationLauncher;
    }
}
