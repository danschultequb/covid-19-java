package qub;

public interface QubCovid19
{
    String applicationName = "qub-covid-19";
    String applicationDescription = "Used to gather, consolidate, and report data about the COVID-19 virus.";

    static void main(String[] args)
    {
        QubProcess.run(args, QubCovid19::run);
    }

    static void run(QubProcess process)
    {
        PreCondition.assertNotNull(process, "process");

        final CommandLineActions<QubProcess> actions = process.<QubProcess>createCommandLineActions()
            .setApplicationName(QubCovid19.applicationName)
            .setApplicationDescription(QubCovid19.applicationDescription);

        actions.addAction(QubCovid19Show.actionName, QubCovid19Show::getParameters, QubCovid19Show::run)
            .setDescription(QubCovid19Show.actionDescription)
            .setDefaultAction();

        actions.addAction(QubCovid19Config.actionName, QubCovid19Config::getParameters, QubCovid19Config::run)
            .setDescription(QubCovid19Config.actionDescription);

        actions.run(process);
    }

    static File getConfigurationFile(Folder dataFolder)
    {
        PreCondition.assertNotNull(dataFolder, "dataFolder");

        return dataFolder.getFile("configuration.json").await();
    }

    static Covid19Configuration getDefaultConfiguration()
    {
        return Covid19Configuration.create()
            .addLocations(
                Covid19Location.create("Global"),
                Covid19Location.create("USA"),
                Covid19Location.create("New York, USA")
                    .setCondition(Covid19LocationCondition.and(
                        Covid19LocationCondition.countryOrRegionEquals("US"),
                        Covid19LocationCondition.or(
                            Covid19LocationCondition.stateOrProvinceEquals("New York"),
                            Covid19LocationCondition.stateOrProvinceContains(", NY")))));
    }

    static String toString(DateTime date)
    {
        PreCondition.assertNotNull(date, "date");

        return date.getMonth() + "/" + date.getDayOfMonth() + "/" + date.getYear();
    }

    static String getActionFullName(String actionName)
    {
        PreCondition.assertNotNullAndNotEmpty(actionName, "actionName");

        return QubCovid19.applicationName + " " + actionName;
    }
}
