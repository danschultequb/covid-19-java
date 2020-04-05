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

        if (!helpParameter.showApplicationHelpLines(process).await())
        {
            profilerParameter.await();

            final CharacterWriteStream output = process.getOutputCharacterWriteStream();

            final Folder projectDataFolder = process.getQubProjectDataFolder().await();
            final Git git = Git.create(process);
            final Covid19DataSource dataSource = Covid19GitDataSource.create(projectDataFolder, git);
            result = new QubCovid19Parameters(output, dataSource);
        }

        return result;
    }

    static void run(QubCovid19Parameters parameters)
    {
        PreCondition.assertNotNull(parameters, "parameters");

        final IndentedCharacterWriteStream output = new IndentedCharacterWriteStream(parameters.getOutput());
        final Covid19DataSource dataSource = parameters.getDataSource();

        output.write("Refreshing data...").await();
        dataSource.refreshData().await();
        output.writeLine(" Done.").await();
        output.writeLine().await();

        final Covid19Summary summary = dataSource.getDataSummary().await();
        final DateTime mostRecentDateReported = summary.getMostRecentDateReported();
        output.writeLine("Summary:").await();
        CharacterTable.create()
            .addRow("Dates reported:", Integers.toString(summary.getDatesReportedCount()))
            .addRow("Countries reported:", Integers.toString(summary.getCountriesReportedCount()))
            .addRow("Most recent report:", QubCovid19.toString(mostRecentDateReported))
            .toString(output, CharacterTableFormat.consise).await();
        output.writeLine().await();
        output.writeLine().await();

        final Iterable<Integer> previousDays = Iterable.create(1, 3, 7, 30);
        final Map<String,Function1<Covid19DailyReportDataRow,Boolean>> locations = Map.<String,Function1<Covid19DailyReportDataRow,Boolean>>create()
            .set("Global", (Covid19DailyReportDataRow row) -> true)
            .set("China", (Covid19DailyReportDataRow row) -> Comparer.equalIgnoreCase(row.getCountryOrRegion(), "China") ||
                                                            Comparer.equalIgnoreCase(row.getCountryOrRegion(), "Mainland China"))
            .set("Italy", (Covid19DailyReportDataRow row) -> Comparer.equalIgnoreCase(row.getCountryOrRegion(), "Italy"))
            .set("South Korea", (Covid19DailyReportDataRow row) -> Comparer.equalIgnoreCase(row.getCountryOrRegion(), "Korea, South"))
            .set("USA", (Covid19DailyReportDataRow row) -> Comparer.equalIgnoreCase(row.getCountryOrRegion(), "US"))
            .set("Washington, USA", (Covid19DailyReportDataRow row) -> (Comparer.equalIgnoreCase(row.getStateOrProvince(), "Washington") ||
                                                     Strings.contains(row.getStateOrProvince(), ", WA")) &&
                                                    Comparer.equalIgnoreCase(row.getCountryOrRegion(), "US"))
            .set("Michigan, USA", (Covid19DailyReportDataRow row) -> (Comparer.equalIgnoreCase(row.getStateOrProvince(), "Michigan") ||
                                                   Strings.contains(row.getStateOrProvince(), ", MI")) &&
                                                  Comparer.equalIgnoreCase(row.getCountryOrRegion(), "US"))
            .set("New York, USA", (Covid19DailyReportDataRow row) -> (Comparer.equalIgnoreCase(row.getStateOrProvince(), "New York") ||
                                                   Strings.contains(row.getStateOrProvince(), ", NY")) &&
                                                  Comparer.equalIgnoreCase(row.getCountryOrRegion(), "US"))
            .set("Florida, USA", (Covid19DailyReportDataRow row) -> (Comparer.equalIgnoreCase(row.getStateOrProvince(), "Florida") ||
                                                  Strings.contains(row.getStateOrProvince(), ", FL")) &&
                                                 Comparer.equalIgnoreCase(row.getCountryOrRegion(), "US"))
            .set("Utah, USA", (Covid19DailyReportDataRow row) -> (Comparer.equalIgnoreCase(row.getStateOrProvince(), "Utah") ||
                                               Strings.contains(row.getStateOrProvince(), ", UT")) &&
                                              Comparer.equalIgnoreCase(row.getCountryOrRegion(), "US"));

        final CharacterTableFormat confirmedCasesFormat = CharacterTableFormat.create()
            .setNewLine('\n')
            .setTopBorder('-')
            .setLeftBorder("| ")
            .setColumnSeparator(" | ")
            .setRightBorder(" |")
            .setBottomBorder('-');

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

    static CharacterTable createConfirmedCasesTable(DateTime reportStartDate, Iterable<Integer> previousDays, Map<String,Function1<Covid19DailyReportDataRow,Boolean>> locations, Covid19DataSource dataSource)
    {
        PreCondition.assertNotNull(reportStartDate, "reportStartDate");
        PreCondition.assertNotNull(previousDays, "previousDays");
        PreCondition.assertNotNull(locations, "locations");
        PreCondition.assertNotNull(dataSource, "dataSource");

        final Indexable<String> locationNames = locations.getKeys().toList();

        final MutableMap<String,List<Integer>> locationDataRows = Map.create();
        for (final String locationName : locationNames)
        {
            locationDataRows.set(locationName, List.create());
        }

        final Covid19DailyReport reportStartDateDailyReport = dataSource.getDailyReport(reportStartDate).await();
        for (final String locationName : locationNames)
        {
            final Function1<Covid19DailyReportDataRow,Boolean> locationCondition = locations.get(locationName).await();
            final int confirmedCases = Integers.sum(reportStartDateDailyReport.getDataRows()
                .where(locationCondition)
                .map(Covid19DailyReportDataRow::getConfirmedCases));
            locationDataRows.get(locationName).await()
                .add(confirmedCases);
        }

        for (final Integer daysAgo : previousDays)
        {
            final DateTime previousDay = reportStartDate.minus(Duration.days(daysAgo));
            final Covid19DailyReport previousDailyReport = dataSource.getDailyReport(previousDay).await();
            for (final String locationName : locationNames)
            {
                final Function1<Covid19DailyReportDataRow,Boolean> locationCondition = locations.get(locationName).await();
                final int confirmedCases = Integers.sum(previousDailyReport.getDataRows()
                    .where(locationCondition)
                    .map(Covid19DailyReportDataRow::getConfirmedCases));
                locationDataRows.get(locationName).await()
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

    static CharacterTable createConfirmedCasesAverageChangePerDayTable(DateTime reportStartDate, Iterable<Integer> previousDays, Map<String,Function1<Covid19DailyReportDataRow,Boolean>> locations, Covid19DataSource dataSource)
    {
        PreCondition.assertNotNull(reportStartDate, "reportStartDate");
        PreCondition.assertNotNull(previousDays, "previousDays");
        PreCondition.assertNotNull(locations, "locations");
        PreCondition.assertNotNull(dataSource, "dataSource");

        final Indexable<String> locationNames = locations.getKeys().toList();

        final MutableMap<String,Integer> locationReportStartDateConfirmedCases = Map.create();
        final Covid19DailyReport dailyReport = dataSource.getDailyReport(reportStartDate).await();
        for (final String locationName : locationNames)
        {
            final Function1<Covid19DailyReportDataRow,Boolean> locationCondition = locations.get(locationName).await();
            final int confirmedCases = Integers.sum(dailyReport.getDataRows()
                .where(locationCondition)
                .map(Covid19DailyReportDataRow::getConfirmedCases));
            locationReportStartDateConfirmedCases.set(locationName, confirmedCases);
        }

        final MutableMap<String,List<Integer>> locationDataRows = Map.create();
        for (final String locationName : locationNames)
        {
            locationDataRows.set(locationName, List.create());
        }
        for (final Integer daysAgo : previousDays)
        {
            final DateTime previousDay = reportStartDate.minus(Duration.days(daysAgo));
            final Covid19DailyReport previousDailyReport = dataSource.getDailyReport(previousDay).await();
            for (final String locationName : locationNames)
            {
                final Function1<Covid19DailyReportDataRow,Boolean> locationCondition = locations.get(locationName).await();
                final int locationReportStartDateConfirmedCasesCount = locationReportStartDateConfirmedCases.get(locationName).await();
                final int previousConfirmedCases = Integers.sum(previousDailyReport.getDataRows()
                    .where(locationCondition)
                    .map(Covid19DailyReportDataRow::getConfirmedCases));
                final int averageConfirmedCasesChangePerDay = (locationReportStartDateConfirmedCasesCount - previousConfirmedCases) / daysAgo;
                locationDataRows.get(locationName).await()
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
