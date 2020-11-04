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
        final CommandLineParameterHelp help = parameters.addHelp();

        if (!help.showApplicationHelpLines(process).await())
        {
            final Folder projectDataFolder = process.getQubProjectDataFolder().await();
            final DefaultApplicationLauncher defaultApplicationLauncher = process.getDefaultApplicationLauncher();

            result = new QubCovid19ConfigParameters(projectDataFolder, defaultApplicationLauncher);
        }

        return result;
    }

    static void run(QubCovid19ConfigParameters parameters)
    {
        PreCondition.assertNotNull(parameters, "parameters");

        final Folder dataFolder = parameters.getDataFolder();
        final DefaultApplicationLauncher defaultApplicationLauncher = parameters.getDefaultApplicationLauncher();

        final File configurationSchemaJsonFile = QubCovid19.getConfigurationSchemaFile(dataFolder);
        try (final CharacterWriteStream configurationSchemaJsonWriteStream = configurationSchemaJsonFile.getContentsCharacterWriteStream().await())
        {
            QubCovid19.getDefaultConfigurationSchema().toString(configurationSchemaJsonWriteStream, JSONFormat.pretty).await();
        }

        final File configurationJsonFile = QubCovid19.getConfigurationFile(dataFolder);
        if (!configurationJsonFile.exists().await())
        {
            try (final CharacterWriteStream configurationJsonWriteStream = configurationJsonFile.getContentsCharacterWriteStream().await())
            {
                QubCovid19.getDefaultConfiguration().toString(configurationJsonWriteStream, JSONFormat.pretty).await();
            }
        }

        defaultApplicationLauncher.openFileWithDefaultApplication(configurationJsonFile).await();
    }
}
