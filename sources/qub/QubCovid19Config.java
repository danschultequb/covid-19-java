package qub;

public interface QubCovid19Config
{
    String actionName = "config";
    String actionDescription = "Open the configuration file for this application.";

    static QubCovid19ConfigParameters getParameters(QubProcess process)
    {
        PreCondition.assertNotNull(process, "process");

        QubCovid19ConfigParameters result = null;

        final CommandLineParameters parameters = process.createCommandLineParameters()
            .setApplicationName(QubCovid19.getActionFullName(QubCovid19Config.actionName))
            .setApplicationDescription(QubCovid19Config.actionDescription);

        final CharacterWriteStream output = process.getOutputWriteStream();
        final Folder projectDataFolder = process.getQubProjectDataFolder().await();
        final DefaultApplicationLauncher defaultApplicationLauncher = process.getDefaultApplicationLauncher();

        return new QubCovid19ConfigParameters(output, projectDataFolder, defaultApplicationLauncher);
    }

    static void run(QubCovid19ConfigParameters parameters)
    {
        PreCondition.assertNotNull(parameters, "parameters");

        final CharacterWriteStream output = parameters.getOutput();
        final Folder dataFolder = parameters.getDataFolder();
        final DefaultApplicationLauncher defaultApplicationLauncher = parameters.getDefaultApplicationLauncher();

        final File configurationJsonFile = QubCovid19.getConfigurationFile(dataFolder);
        if (!configurationJsonFile.exists().await())
        {
            try (final CharacterWriteStream configurationJsonWriteStream = configurationJsonFile.getContentCharacterWriteStream().await())
            {
                QubCovid19.getDefaultConfiguration().toString(configurationJsonWriteStream, JSONFormat.pretty).await();
            }
        }

        defaultApplicationLauncher.openFileWithDefaultApplication(configurationJsonFile).await();
    }
}
