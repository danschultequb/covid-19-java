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
            final HttpClient httpClient = HttpClient.create(process.getNetwork());
            final DateTime now = process.getClock().getCurrentDateTime();
            result = new QubCovid19Parameters(output, httpClient, now);
        }

        return result;
    }

    static void run(QubCovid19Parameters parameters)
    {
        PreCondition.assertNotNull(parameters, "parameters");

        final IndentedCharacterWriteStream output = new IndentedCharacterWriteStream(parameters.getOutput());
        final HttpClient httpClient = parameters.getHttpClient();
        final DateTime now = parameters.getNow();

        final String confirmedUrlString = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_19-covid-Confirmed.csv";

        output.write("Getting confirmed cases data...").await();
        try (final HttpResponse confirmedResponse = httpClient.get(confirmedUrlString).await())
        {
            output.writeLine(" Done.").await();

            output.write("Parsing confirmed cases data...");
            final Covid19DataDocument data = Covid19DataDocument.parse(confirmedResponse.getBody()).await();
            output.writeLine(" Done.").await();
            output.writeLine().await();

            output.writeLine("Summary:").await();
            final CharacterTable summary = CharacterTable.create();
            final Iterable<DateTime> datesReported = data.getDatesReported(now);
            summary.addRow("Dates reported:", Integers.toString(datesReported.getCount()));
            final Iterable<String> countriesReported = data.getCountriesReported(now);
            summary.addRow("Countries reported:", Integers.toString(countriesReported.getCount()));
            final DateTime mostRecentDateReported = datesReported.last();
            summary.addRow("Most recent report:", QubCovid19.toString(mostRecentDateReported));
            summary.toString(output, CharacterTableFormat.consise).await();
            output.writeLine().await();
            output.writeLine().await();

            final DateTime reportStartDate = Comparer.minimum(Iterable.create(now, mostRecentDateReported));

            final Iterable<Integer> previousDays = Iterable.create(1, 3, 7, 30);
            final Map<String,Function1<CSVRow,Boolean>> locations = Map.<String,Function1<CSVRow,Boolean>>create()
                .set("Global", (CSVRow row) -> true)
                .set("China", (CSVRow row) -> Comparer.equalIgnoreCase(row.getCell(1), "China"))
                .set("USA", (CSVRow row) -> Comparer.equalIgnoreCase(row.getCell(1), "US"))
                .set("UK", (CSVRow row) -> Comparer.equalIgnoreCase(row.getCell(1), "United Kingdom"));

            output.writeLine("Confirmed Cases:").await();
            final CharacterTableFormat confirmedCasesFormat = CharacterTableFormat.create()
                .setNewLine('\n')
                .setTopBorder('-')
                .setLeftBorder("| ")
                .setColumnSeparator(" | ")
                .setRightBorder(" |")
                .setBottomBorder('-');
            final CharacterTable confirmedCasesTable = CharacterTable.create();
            final List<String> confirmedCasesHeaderRow = List.create("Location", QubCovid19.toString(reportStartDate));
            confirmedCasesHeaderRow.addAll(previousDays.map((Integer daysAgo) -> daysAgo + " days ago"));
            confirmedCasesTable.addRow(confirmedCasesHeaderRow);
            final Action2<String,Function1<CSVRow,Boolean>> addConfirmedCasesRow = (String location, Function1<CSVRow,Boolean> rowCondition) ->
            {
                final List<String> confirmedCasesRow = List.create(location);
                final int totalConfirmedCases = data.getConfirmedCases(reportStartDate, rowCondition);
                confirmedCasesRow.add(Integers.toString(totalConfirmedCases));
                for (final int daysAgo : previousDays)
                {
                    final DateTime previousDate = reportStartDate.minus(Duration.days(daysAgo));
                    confirmedCasesRow.add(Integers.toString(data.getConfirmedCases(previousDate, rowCondition)));
                }
                confirmedCasesTable.addRow(confirmedCasesRow);
            };
            for (final MapEntry<String,Function1<CSVRow,Boolean>> location : locations)
            {
                addConfirmedCasesRow.run(location.getKey(), location.getValue());
            }
            confirmedCasesTable.toString(output, confirmedCasesFormat).await();
            output.writeLine().await();
            output.writeLine().await();

            output.writeLine("Confirmed Cases Change:").await();
            final CharacterTable confirmedCasesChangeTable = CharacterTable.create();
            final List<String> confirmedCasesChangeHeaderRow = List.create("Location");
            confirmedCasesChangeHeaderRow.addAll(previousDays.map((Integer daysAgo) -> daysAgo + " days ago"));
            confirmedCasesChangeTable.addRow(confirmedCasesChangeHeaderRow);
            final Action2<String,Function1<CSVRow,Boolean>> addConfirmedCasesChangeRow = (String location, Function1<CSVRow,Boolean> rowCondition) ->
            {
                final List<String> confirmedCasesChangeRow = List.create(location);
                final int totalConfirmedCases = data.getConfirmedCases(reportStartDate, rowCondition);
                for (final int daysAgo : previousDays)
                {
                    final DateTime previousDate = reportStartDate.minus(Duration.days(daysAgo));
                    final int previousDateConfirmedCases = data.getConfirmedCases(previousDate, rowCondition);
                    final int confirmedCasesChange = totalConfirmedCases - previousDateConfirmedCases;
                    confirmedCasesChangeRow.add(Integers.toString(confirmedCasesChange));
                }
                confirmedCasesChangeTable.addRow(confirmedCasesChangeRow);
            };
            for (final MapEntry<String,Function1<CSVRow,Boolean>> location : locations)
            {
                addConfirmedCasesChangeRow.run(location.getKey(), location.getValue());
            }
            confirmedCasesChangeTable.toString(output, confirmedCasesFormat).await();
            output.writeLine();
            output.writeLine();

            output.writeLine("Confirmed Cases Average Change Per Day:").await();
            final CharacterTable confirmedCasesAverageChangePerDayTable = CharacterTable.create();
            confirmedCasesAverageChangePerDayTable.addRow(confirmedCasesChangeHeaderRow);
            final Action2<String,Function1<CSVRow,Boolean>> addConfirmedCasesAverageChangePerDayRow = (String location, Function1<CSVRow,Boolean> rowCondition) ->
            {
                final List<String> confirmedCasesAverageChangePerDayRow = List.create(location);
                final int totalConfirmedCases = data.getConfirmedCases(reportStartDate, rowCondition);
                for (final int daysAgo : previousDays)
                {
                    final DateTime previousDate = reportStartDate.minus(Duration.days(daysAgo));
                    final int previousDateConfirmedCases = data.getConfirmedCases(previousDate, rowCondition);
                    final int confirmedCasesChange = totalConfirmedCases - previousDateConfirmedCases;
                    final int confirmedCasesAverageChangePerDay = confirmedCasesChange / daysAgo;
                    confirmedCasesAverageChangePerDayRow.add(Integers.toString(confirmedCasesAverageChangePerDay));
                }
                confirmedCasesAverageChangePerDayTable.addRow(confirmedCasesAverageChangePerDayRow);
            };
            for (final MapEntry<String,Function1<CSVRow,Boolean>> location : locations)
            {
                addConfirmedCasesAverageChangePerDayRow.run(location.getKey(), location.getValue());
            }
            confirmedCasesAverageChangePerDayTable.toString(output, confirmedCasesFormat).await();
            output.writeLine();
        }
    }

    static String toString(DateTime date)
    {
        PreCondition.assertNotNull(date, "date");

        return date.getMonth() + "/" + date.getDayOfMonth() + "/" + date.getYear();
    }
}
