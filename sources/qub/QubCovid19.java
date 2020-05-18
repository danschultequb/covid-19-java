package qub;

public interface QubCovid19
{
    static void main(String[] args)
    {
        QubProcess.run(args, QubCovid19::getParameters, QubCovid19::run);
    }

    /**
     * Get the application parameters object.
     * @param process The process that is running the application.
     * @return The application parameters object or null if the application should not run.
     */
    static QubCovid19Parameters getParameters(QubProcess process)
    {
        PreCondition.assertNotNull(process, "process");

        QubCovid19Parameters result = null;

        final CommandLineParameters parameters = process.createCommandLineParameters()
            .setApplicationName("qub/covid-19-java")
            .setApplicationDescription("Used to gather, consolidate, and report data about the COVID-19 virus.");
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
            result = new QubCovid19Parameters(output, verbose, dataSource);
        }

        return result;
    }

    static void run(QubCovid19Parameters parameters)
    {
        PreCondition.assertNotNull(parameters, "parameters");

        final IndentedCharacterWriteStream output = IndentedCharacterWriteStream.create(parameters.getOutput());
        final Covid19DataSource dataSource = parameters.getDataSource();

        output.write("Refreshing data...").await();
        dataSource.refreshData().await();
        output.writeLine(" Done.").await();
        output.writeLine().await();

        final Covid19Summary summary = dataSource.getDataSummary().await();
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

        final Iterable<Integer> previousDays = Iterable.create(1, 3, 7, 30);
        final Iterable<Covid19Location> locations = Iterable.create(
            Covid19Location.create("Global"),
            Covid19Location.create("China")
                .setCondition(Covid19LocationCondition.or(
                    Covid19LocationCondition.countryOrRegionEquals("China"),
                    Covid19LocationCondition.countryOrRegionEquals("Mainland China"))),
            Covid19Location.create("Italy")
                .setCondition(Covid19LocationCondition.countryOrRegionEquals("Italy")),
            Covid19Location.create("South Korea")
                .setCondition(Covid19LocationCondition.countryOrRegionEquals("Korea, South")),
            Covid19Location.create("USA")
                .setCondition(Covid19LocationCondition.countryOrRegionEquals("US")),
            Covid19Location.create("Washington, USA")
                .setCondition(Covid19LocationCondition.and(
                    Covid19LocationCondition.countryOrRegionEquals("US"),
                    Covid19LocationCondition.or(
                        Covid19LocationCondition.stateOrProvinceEquals("Washington"),
                        Covid19LocationCondition.stateOrProvinceContains(", WA")))),
            Covid19Location.create("Michigan, USA")
                .setCondition(Covid19LocationCondition.and(
                    Covid19LocationCondition.countryOrRegionEquals("US"),
                    Covid19LocationCondition.or(
                        Covid19LocationCondition.stateOrProvinceEquals("Michigan"),
                        Covid19LocationCondition.stateOrProvinceContains(", MI")))),
            Covid19Location.create("New York, USA")
                .setCondition(Covid19LocationCondition.and(
                    Covid19LocationCondition.countryOrRegionEquals("US"),
                    Covid19LocationCondition.or(
                        Covid19LocationCondition.stateOrProvinceEquals("New York"),
                        Covid19LocationCondition.stateOrProvinceContains(", NY")))),
            Covid19Location.create("Florida, USA")
                .setCondition(Covid19LocationCondition.and(
                    Covid19LocationCondition.countryOrRegionEquals("US"),
                    Covid19LocationCondition.or(
                        Covid19LocationCondition.stateOrProvinceEquals("Florida"),
                        Covid19LocationCondition.stateOrProvinceContains(", FL")))),
            Covid19Location.create("Utah, USA")
                .setCondition(Covid19LocationCondition.and(
                    Covid19LocationCondition.countryOrRegionEquals("US"),
                    Covid19LocationCondition.or(
                        Covid19LocationCondition.stateOrProvinceEquals("Utah"),
                        Covid19LocationCondition.stateOrProvinceContains(", UT")))),
            Covid19Location.create("Utah County, UT, USA")
                .setCondition(Covid19LocationCondition.and(
                    Covid19LocationCondition.countryOrRegionEquals("US"),
                    Covid19LocationCondition.or(
                        Covid19LocationCondition.stateOrProvinceEquals("Utah"),
                        Covid19LocationCondition.stateOrProvinceContains(", UT")),
                    Covid19LocationCondition.countyEquals("Utah"))));

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
        final CharacterTable confirmedCasesTable = QubCovid19.createConfirmedCasesTable(mostRecentDateReported, previousDays, locations, dataSource);
        confirmedCasesTable.toString(output, confirmedCasesFormat).await();
        output.writeLine().await();
        output.writeLine().await();

        output.writeLine("Confirmed Cases Average Change Per Day:").await();
        final CharacterTable confirmedCasesAverageChangePerDayTable = QubCovid19.createConfirmedCasesAverageChangePerDayTable(mostRecentDateReported, previousDays, locations, dataSource);
        confirmedCasesAverageChangePerDayTable.toString(output, confirmedCasesFormat).await();
        output.writeLine().await();
    }

    static CharacterTable createConfirmedCasesTable(DateTime reportStartDate, Iterable<Integer> previousDays, Iterable<Covid19Location> locations, Covid19DataSource dataSource)
    {
        PreCondition.assertNotNull(reportStartDate, "reportStartDate");
        PreCondition.assertNotNull(previousDays, "previousDays");
        PreCondition.assertNotNull(locations, "locations");
        PreCondition.assertNotNull(dataSource, "dataSource");

        final MutableMap<String,List<Integer>> locationDataRows = Map.create();
        for (final Covid19Location location : locations)
        {
            locationDataRows.set(location.getName(), List.create());
        }

        final Covid19DailyReport reportStartDateDailyReport = dataSource.getDailyReport(reportStartDate).await();
        for (final Covid19Location location : locations)
        {
            final int confirmedCases = Integers.sum(reportStartDateDailyReport.getDataRows()
                .where(location::matches)
                .map(Covid19DailyReportDataRow::getConfirmedCases));
            locationDataRows.get(location.getName()).await()
                .add(confirmedCases);
        }

        for (final Integer daysAgo : previousDays)
        {
            final DateTime previousDay = reportStartDate.minus(Duration.days(daysAgo));
            final Covid19DailyReport previousDailyReport = dataSource.getDailyReport(previousDay).await();
            for (final Covid19Location location : locations)
            {
                final int confirmedCases = Integers.sum(previousDailyReport.getDataRows()
                    .where(location::matches)
                    .map(Covid19DailyReportDataRow::getConfirmedCases));
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

    static CharacterTable createConfirmedCasesAverageChangePerDayTable(DateTime reportStartDate, Iterable<Integer> previousDays, Iterable<Covid19Location> locations, Covid19DataSource dataSource)
    {
        PreCondition.assertNotNull(reportStartDate, "reportStartDate");
        PreCondition.assertNotNull(previousDays, "previousDays");
        PreCondition.assertNotNull(locations, "locations");
        PreCondition.assertNotNull(dataSource, "dataSource");

        final MutableMap<String,Integer> locationReportStartDateConfirmedCases = Map.create();
        final Covid19DailyReport dailyReport = dataSource.getDailyReport(reportStartDate).await();
        for (final Covid19Location location : locations)
        {
            final int confirmedCases = Integers.sum(dailyReport.getDataRows()
                .where(location::matches)
                .map(Covid19DailyReportDataRow::getConfirmedCases));
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
            final Covid19DailyReport previousDailyReport = dataSource.getDailyReport(previousDay).await();
            for (final Covid19Location location : locations)
            {
                final int locationReportStartDateConfirmedCasesCount = locationReportStartDateConfirmedCases.get(location.getName()).await();
                final int previousConfirmedCases = Integers.sum(previousDailyReport.getDataRows()
                    .where(location::matches)
                    .map(Covid19DailyReportDataRow::getConfirmedCases));
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
