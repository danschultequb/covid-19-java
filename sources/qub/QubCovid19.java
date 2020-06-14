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

    static File getConfigurationSchemaFile(Folder dataFolder)
    {
        PreCondition.assertNotNull(dataFolder, "dataFolder");

        return dataFolder.getFile("configuration.schema.json").await();
    }

    static File getConfigurationFile(Folder dataFolder)
    {
        PreCondition.assertNotNull(dataFolder, "dataFolder");

        return dataFolder.getFile("configuration.json").await();
    }

    static JSONSchema getDefaultConfigurationSchema()
    {
        return JSONSchema.create()
            .setSchema("http://json-schema.org/draft-04/schema")
            .setType(JSONSchemaType.Object)
            .addProperty(JSONSchema.schemaPropertyName, JSONSchema.create()
                .setDescription("The schema that defines how a qub-covid-19 configuration file should be structured.")
                .setEnum("./configuration.schema.json")
            )
            .addProperty("locations", JSONSchema.create()
                .setDescription("The locations that will be displayed when the qub-covid-19 application is run.")
                .setType(JSONSchemaType.Object)
                .setAdditionalProperties(JSONSchema.create()
                    .setRef("#/definitions/locationCondition")
                )
            )
            .setRequired("$schema", "locations")
            .setAdditionalProperties(false)
            .addDefinition("locationCondition", JSONSchema.create()
                .setOneOf(
                    JSONSchema.create()
                        .setDescription("A location condition that matches all Covid-19 data rows.")
                        .setType(JSONSchemaType.Object)
                        .setAdditionalProperties(false),
                    JSONSchema.create()
                        .setDescription("A location condition that groups together other location conditions in a boolean expression.")
                        .setType(JSONSchemaType.Object)
                        .addProperty("operator", JSONSchema.create()
                            .setDescription("The operator that will be used to combine the provided location conditions.")
                            .setEnum("And", "Or")
                        )
                        .addProperty("conditions", JSONSchema.create()
                            .setDescription("The location conditions that will be evaluated and grouped together.")
                            .setType(JSONSchemaType.Array)
                            .setItems(JSONSchema.create()
                                .setRef("#/definitions/locationCondition")
                            )
                        )
                        .setRequired("operator", "conditions")
                        .setAdditionalProperties(false),
                    JSONSchema.create()
                        .setDescription("A location condition that evaluates an expression based on the properties of a Covid-19 data row.")
                        .setType(JSONSchemaType.Object)
                        .addProperty("propertyName", JSONSchema.create()
                            .setDescription("The name of the property that will be checked.")
                            .setEnum("countryOrRegion", "stateOrProvince", "county")
                        )
                        .addProperty("operator", JSONSchema.create()
                            .setDescription("The operator to use when checking the property.")
                            .setEnum("Equals", "Contains")
                        )
                        .addProperty("expectedPropertyValue", JSONSchema.create()
                            .setDescription("The expected property value to check against.")
                            .setType(JSONSchemaType.String)
                            .setMinLength(1)
                        )
                        .setRequired("propertyName", "operator", "expectedPropertyValue")
                )
            );
    }

    static Covid19Configuration getDefaultConfiguration()
    {
        return Covid19Configuration.create()
            .addLocations(
                Covid19Location.create("Global"),
                Covid19Location.create("USA",
                    Covid19LocationCondition.countryOrRegionEquals("US")),
                Covid19Location.create("New York, USA",
                    Covid19LocationCondition.and(
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
