package qub;

import java.util.Objects;

public interface QubCovid19Show
{
    String actionName = "show";
    String actionDescription = "Report the current state of the COVID-19 virus in the configured locations.";

    static QubCovid19ShowParameters getParameters(QubProcess process)
    {
        PreCondition.assertNotNull(process, "process");

        QubCovid19ShowParameters result = null;

        final CommandLineParameters parameters = process.createCommandLineParameters()
            .setApplicationName(QubCovid19.getActionFullName(QubCovid19Show.actionName))
            .setApplicationDescription(QubCovid19Show.actionDescription);
        final CommandLineParameterProfiler profilerParameter = parameters.addProfiler(process, QubCovid19.class);
        final CommandLineParameterHelp helpParameter = parameters.addHelp();
        final CommandLineParameterVerbose verboseParameter = parameters.addVerbose(process);

        if (!helpParameter.showApplicationHelpLines(process).await())
        {
            profilerParameter.await();

            final CharacterWriteStream output = process.getOutputWriteStream();
            final VerboseCharacterWriteStream verbose = verboseParameter.getVerboseCharacterWriteStream().await();

            final Folder projectDataFolder = process.getQubProjectDataFolder().await();
            final Git git = Git.create(process);
            final Covid19DataSource dataSource = Covid19GitDataSource.create(projectDataFolder, git, verbose);
            result = new QubCovid19ShowParameters(output, verbose, projectDataFolder, dataSource);
        }

        return result;
    }

    static void run(QubCovid19ShowParameters parameters)
    {
        PreCondition.assertNotNull(parameters, "parameters");

        final IndentedCharacterWriteStream output = IndentedCharacterWriteStream.create(parameters.getOutput());
        final Folder dataFolder = parameters.getDataFolder();
        final Covid19DataSource dataSource = parameters.getDataSource();

        output.write("Refreshing data...").await();
        dataSource.refreshData().await();
        output.writeLine(" Done.").await();
        output.writeLine().await();

        final Set<Covid19Issue> issues = Set.create();
        final Covid19Summary summary = dataSource.getDataSummary(issues::add).await();
        final DateTime mostRecentDateReported = summary.getMostRecentDateReported();
        final CharacterTableFormat summaryFormat = CharacterTableFormat.create()
            .setNewLine('\n')
            .setColumnSeparator(' ')
            .setColumnHorizontalAlignment(1, HorizontalAlignment.Right);
        output.writeLine("Summary:").await();
        CharacterTable.create()
            .addRow("Dates reported:", Integers.toString(summary.getDatesReportedCount()))
            .addRow("Countries reported:", Integers.toString(summary.getCountriesReportedCount()))
            .addRow("Most recent report:", QubCovid19.toString(mostRecentDateReported))
            .toString(output, summaryFormat).await();
        output.writeLine().await();
        output.writeLine().await();

        final File configurationJsonFile = QubCovid19.getConfigurationFile(dataFolder);
        Covid19Configuration configuration = Covid19Configuration.parse(configurationJsonFile)
            .catchError(FileNotFoundException.class)
            .await();
        if (configuration == null)
        {
            configuration = QubCovid19.getDefaultConfiguration();
            try (final CharacterWriteStream configurationJsonWriteStream = configurationJsonFile.getContentsCharacterWriteStream().await())
            {
                configuration.toString(configurationJsonWriteStream, JSONFormat.pretty).await();
            }
        }

        final Iterable<Covid19Location> locations = configuration.getLocations();

        final Iterable<Integer> previousDays = Iterable.create(1, 3, 7, 30);

        final CharacterTableFormat confirmedCasesFormat = CharacterTableFormat.create()
            .setNewLine('\n')
            .setTopBorder('-')
            .setLeftBorder("| ")
            .setColumnSeparator(" | ")
            .setRightBorder(" |")
            .setBottomBorder('-');

        final int previousDaysCount = previousDays.getCount();
        for (int i = 0; i < previousDaysCount + 1; ++i)
        {
            confirmedCasesFormat.setColumnHorizontalAlignment(i + 1, HorizontalAlignment.Right);
        }

        output.writeLine("Confirmed Cases:").await();
        final CharacterTable confirmedCasesTable = QubCovid19Show.createConfirmedCasesTable(mostRecentDateReported, previousDays, locations, dataSource, issues::add);
        confirmedCasesTable.toString(output, confirmedCasesFormat).await();
        output.writeLine().await();
        output.writeLine().await();

        output.writeLine("Confirmed Cases Average Change Per Day:").await();
        final CharacterTable confirmedCasesAverageChangePerDayTable = QubCovid19Show.createConfirmedCasesAverageChangePerDayTable(mostRecentDateReported, previousDays, locations, dataSource, issues::add);
        confirmedCasesAverageChangePerDayTable.toString(output, confirmedCasesFormat).await();
        output.writeLine().await();

        if (issues.any())
        {
            output.writeLine().await();

            output.writeLine("Issues:").await();
            int issueNumber = 0;
            for (final Covid19Issue issue : issues)
            {
                ++issueNumber;

                output.writeLine(issueNumber + ". " + issue.getMessage()).await();
            }
        }

        output.writeLine().await();
    }

    static CharacterTable createConfirmedCasesTable(DateTime reportStartDate, Iterable<Integer> previousDays, Iterable<Covid19Location> locations, Covid19DataSource dataSource, Action1<Covid19Issue> onIssue)
    {
        PreCondition.assertNotNull(reportStartDate, "reportStartDate");
        PreCondition.assertNotNull(previousDays, "previousDays");
        PreCondition.assertNotNull(locations, "locations");
        PreCondition.assertNotNull(dataSource, "dataSource");
        PreCondition.assertNotNull(onIssue, "onIssue");

        final MutableMap<String,List<Integer>> locationDataRows = Map.create();
        for (final Covid19Location location : locations)
        {
            locationDataRows.set(location.getName(), List.create());
        }

        final Covid19DailyReport reportStartDateDailyReport = dataSource.getDailyReport(reportStartDate, onIssue).await();
        for (final Covid19Location location : locations)
        {
            final int confirmedCases = Integers.sum(reportStartDateDailyReport.getDataRows()
                .where(location::matches)
                .map(Covid19DailyReportDataRow::getConfirmedCases)
                .where(Objects::nonNull));
            locationDataRows.get(location.getName()).await()
                .add(confirmedCases);
        }

        for (final Integer daysAgo : previousDays)
        {
            final DateTime previousDay = reportStartDate.minus(Duration.days(daysAgo));
            final Covid19DailyReport previousDailyReport = dataSource.getDailyReport(previousDay, onIssue).await();
            for (final Covid19Location location : locations)
            {
                final int confirmedCases = Integers.sum(previousDailyReport.getDataRows()
                    .where(location::matches)
                    .map(Covid19DailyReportDataRow::getConfirmedCases)
                    .where(Objects::nonNull));
                locationDataRows.get(location.getName()).await()
                    .add(confirmedCases);
            }
        }

        final CharacterTable result = CharacterTable.create();

        final List<String> headerRow = List.create("Location");
        headerRow.add(QubCovid19.toString(reportStartDate));
        if (!Iterable.isNullOrEmpty(previousDays))
        {
            headerRow.addAll(previousDays.map((Integer daysAgo) -> daysAgo + " days ago"));
        }
        result.addRow(headerRow);

        for (final MapEntry<String,List<Integer>> locationRowData : locationDataRows)
        {
            result.addRow(List.create(locationRowData.getKey())
                .addAll(locationRowData.getValue().map(Integers::toString)));
        }

        PostCondition.assertNotNull(result, "result");

        return result;
    }

    static CharacterTable createConfirmedCasesAverageChangePerDayTable(DateTime reportStartDate, Iterable<Integer> previousDays, Iterable<Covid19Location> locations, Covid19DataSource dataSource, Action1<Covid19Issue> onIssue)
    {
        PreCondition.assertNotNull(reportStartDate, "reportStartDate");
        PreCondition.assertNotNull(previousDays, "previousDays");
        PreCondition.assertNotNull(locations, "locations");
        PreCondition.assertNotNull(dataSource, "dataSource");
        PreCondition.assertNotNull(onIssue, "onIssue");

        final MutableMap<String,Integer> locationReportStartDateConfirmedCases = Map.create();
        final Covid19DailyReport dailyReport = dataSource.getDailyReport(reportStartDate, onIssue).await();
        for (final Covid19Location location : locations)
        {
            final int confirmedCases = Integers.sum(dailyReport.getDataRows()
                .where(location::matches)
                .map(Covid19DailyReportDataRow::getConfirmedCases)
                .where(Objects::nonNull));
            locationReportStartDateConfirmedCases.set(location.getName(), confirmedCases);
        }

        final MutableMap<String,List<Integer>> locationDataRows = Map.create();
        for (final Covid19Location location : locations)
        {
            locationDataRows.set(location.getName(), List.create());
        }

        for (final Integer daysAgo : previousDays)
        {
            final DateTime previousDay = reportStartDate.minus(Duration.days(daysAgo));
            final Covid19DailyReport previousDailyReport = dataSource.getDailyReport(previousDay, onIssue).await();
            for (final Covid19Location location : locations)
            {
                final int locationReportStartDateConfirmedCasesCount = locationReportStartDateConfirmedCases.get(location.getName()).await();
                final int previousConfirmedCases = Integers.sum(previousDailyReport.getDataRows()
                    .where(location::matches)
                    .map(Covid19DailyReportDataRow::getConfirmedCases)
                    .where(Objects::nonNull));
                final int averageConfirmedCasesChangePerDay = (locationReportStartDateConfirmedCasesCount - previousConfirmedCases) / daysAgo;
                locationDataRows.get(location.getName()).await()
                    .add(averageConfirmedCasesChangePerDay);
            }
        }

        final CharacterTable result = CharacterTable.create();

        final List<String> headerRow = List.create("Location");
        if (!Iterable.isNullOrEmpty(previousDays))
        {
            headerRow.addAll(previousDays.map((Integer daysAgo) -> daysAgo + " days ago"));
        }
        result.addRow(headerRow);

        for (final MapEntry<String,List<Integer>> locationRowData : locationDataRows)
        {
            result.addRow(List.create(locationRowData.getKey())
                .addAll(locationRowData.getValue().map(Integers::toString)));
        }

        PostCondition.assertNotNull(result, "result");

        return result;
    }

    static String toString(DateTime date)
    {
        PreCondition.assertNotNull(date, "date");

        return date.getMonth() + "/" + date.getDayOfMonth() + "/" + date.getYear();
    }
}
