package qub;

/**
 * Parameters for the "config" action in the qub/covid-19 application.
 */
public class QubCovid19ConfigParameters
{
    private final Folder dataFolder;
    private final DefaultApplicationLauncher defaultApplicationLauncher;

    public QubCovid19ConfigParameters(Folder dataFolder, DefaultApplicationLauncher defaultApplicationLauncher)
    {
        PreCondition.assertNotNull(dataFolder, "dataFolder");
        PreCondition.assertNotNull(defaultApplicationLauncher, "defaultApplicationLauncher");

        this.dataFolder = dataFolder;
        this.defaultApplicationLauncher = defaultApplicationLauncher;
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
